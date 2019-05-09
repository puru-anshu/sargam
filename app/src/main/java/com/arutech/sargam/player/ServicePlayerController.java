package com.arutech.sargam.player;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;

import com.arutech.sargam.IPlayerService;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.data.store.ImmutablePreferenceStore;
import com.arutech.sargam.data.store.PreferenceStore;
import com.arutech.sargam.data.store.ReadOnlyPreferenceStore;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.utils.ObservableQueue;
import com.arutech.sargam.utils.Optional;
import com.arutech.sargam.utils.Util;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

/**
 * An implementation of {@link PlayerController} used in release builds to communicate with the
 * media player throughout the application. This implementation uses AIDL to send commands through
 * IPC to the remote player service, and gets information with a combination of AIDL to fetch data
 * and BroadcastReceivers to be notified of automatic changes to the player state.
 * <p>
 * This class is responsible for all communication to the remote service, including starting,
 * binding, unbinding and restarting the service if it crashes.
 */
public class ServicePlayerController implements PlayerController {

	private static final int POSITION_TICK_MS = 200;
	private static final int SERVICE_RESTART_THRESHOLD_MS = 500;

	private Context mContext;
	private IPlayerService mBinding;
	private long mServiceStartRequestTime;

	private PublishSubject<String> mErrorStream = PublishSubject.create();
	private PublishSubject<String> mInfoStream = PublishSubject.create();

	private final Prop<Boolean> mPlaying = new Prop<>("playing");
	private final Prop<Song> mNowPlaying = new Prop<>("now playing");
	private final Prop<List<Song>> mQueue = new Prop<>("queue", Collections.emptyList());
	private final Prop<Integer> mQueuePosition = new Prop<>("queue index");
	private final Prop<Integer> mCurrentPosition = new Prop<>("seek position");
	private final Prop<Integer> mDuration = new Prop<>("duration");
	private final Prop<Integer> mMultiRepeatCount = new Prop<>("multi-repeat");
	private final Prop<Long> mSleepTimerEndTime = new Prop<>("sleep timer");

	private BehaviorSubject<Boolean> mShuffled;
	private BehaviorSubject<Bitmap> mArtwork;
	private Disposable mCurrentPositionClock;

	private ObservableQueue<Runnable> mRequestQueue;
	private Disposable mRequestQueueSubscription;

	public ServicePlayerController(Context context, PreferenceStore preferenceStore) {
		mContext = context;
		mShuffled = BehaviorSubject.createDefault(preferenceStore.isShuffled());
		mRequestQueue = new ObservableQueue<>();

		startService();

		isPlaying().subscribe(
				isPlaying -> {
					if (isPlaying) {
						startCurrentPositionClock();
					} else {
						stopCurrentPositionClock();
					}
				}, throwable -> {
					Timber.e(throwable, "Failed to update current position clock");
				});
	}

	private void startService() {
		bindService(true);

	}

	private void bindService(boolean hasMediaStorePermission) {
		long timeSinceLastStartRequest = SystemClock.uptimeMillis() - mServiceStartRequestTime;

		if (!hasMediaStorePermission || mBinding != null
				|| timeSinceLastStartRequest < SERVICE_RESTART_THRESHOLD_MS) {
			return;
		}

		Intent serviceIntent = PlayerService.newIntent(mContext, true);

		// Manually start the service to ensure that it is associated with this task and can
		// appropriately set its dismiss behavior
		mContext.startService(serviceIntent);
		mServiceStartRequestTime = SystemClock.uptimeMillis();

		mContext.bindService(serviceIntent, new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mBinding = IPlayerService.Stub.asInterface(service);
				initAllProperties();
				bindRequestQueue();
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				mContext.unbindService(this);
				releaseAllProperties();
				mServiceStartRequestTime = 0;
				mBinding = null;
				if (mRequestQueueSubscription != null) {
					mRequestQueueSubscription.dispose();
					mRequestQueueSubscription = null;
				}
			}
		}, Context.BIND_WAIVE_PRIORITY);
	}

	private void ensureServiceStarted() {
		if (mBinding == null) {
			startService();
		}
	}

	private void bindRequestQueue() {
		mRequestQueueSubscription = mRequestQueue.toObservable()
				.subscribe(Runnable::run, throwable -> {
					Timber.e(throwable, "Failed to process request");
					// Make sure to restart the request queue, otherwise all future commands will
					// be dropped
					bindRequestQueue();
				});
	}

	private void execute(Runnable command) {
		ensureServiceStarted();
		mRequestQueue.enqueue(command);
	}

	private void startCurrentPositionClock() {
		if (mCurrentPositionClock != null && !mCurrentPositionClock.isDisposed()) {
			return;
		}

		mCurrentPositionClock = Observable.interval(POSITION_TICK_MS, TimeUnit.MILLISECONDS)
				.observeOn(Schedulers.computation())
				.subscribe(tick -> {
					if (!mCurrentPosition.isSubscribedTo()) {
						stopCurrentPositionClock();
					} else {
						mCurrentPosition.invalidate();
					}
				}, throwable -> {
					Timber.e(throwable, "Failed to perform position tick");
				});
	}

	private void stopCurrentPositionClock() {
		if (mCurrentPositionClock != null) {
			mCurrentPositionClock.dispose();
			mCurrentPositionClock = null;
		}
	}

	private void releaseAllProperties() {
		mPlaying.setFunction(null);
		mNowPlaying.setFunction(null);
		mQueue.setFunction(null);
		mQueuePosition.setFunction(null);
		mCurrentPosition.setFunction(null);
		mDuration.setFunction(null);
		mMultiRepeatCount.setFunction(null);
		mSleepTimerEndTime.setFunction(null);
	}

	private void initAllProperties() {
		mPlaying.setFunction(mBinding::isPlaying);
		mNowPlaying.setFunction(mBinding::getNowPlaying);
		mQueue.setFunction(mBinding::getQueue);
		mQueuePosition.setFunction(mBinding::getQueuePosition);
		mCurrentPosition.setFunction(mBinding::getCurrentPosition);
		mDuration.setFunction(mBinding::getDuration);
		mMultiRepeatCount.setFunction(mBinding::getMultiRepeatCount);
		mSleepTimerEndTime.setFunction(mBinding::getSleepTimerEndTime);

		invalidateAll();
	}

	private void invalidateAll() {
		mPlaying.invalidate();
		mNowPlaying.invalidate();
		mQueue.invalidate();
		mQueuePosition.invalidate();
		mCurrentPosition.invalidate();
		mDuration.invalidate();
		mMultiRepeatCount.invalidate();
		mSleepTimerEndTime.invalidate();
	}

	@Override
	public Observable<String> getError() {
		return mErrorStream;
	}

	@Override
	public Observable<String> getInfo() {
		return mInfoStream;
	}

	@Override
	public Single<PlayerState> getPlayerState() {
		return Observable.fromCallable(mBinding::getPlayerState).single(new PlayerState.Builder().build());
	}

	@Override
	public void restorePlayerState(PlayerState restoreState) {
		execute(() -> {
			try {
				mBinding.restorePlayerState(restoreState);
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to restore player state");
			}
		});
	}

	@Override
	public void stop() {
		execute(() -> {
			try {
				mBinding.stop();
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to stop service");
			}
		});
	}

	@Override
	public void skip() {
		execute(() -> {
			try {
				mBinding.skip();
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to skip current track");
			}
		});
	}

	@Override
	public void previous() {
		execute(() -> {
			try {
				mBinding.previous();
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to skip backward");
			}
		});
	}

	@Override
	public void togglePlay() {
		execute(() -> {
			try {
				mBinding.togglePlay();
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to toggle playback");
			}
		});
	}

	@Override
	public void play() {
		execute(() -> {
			try {
				mBinding.play();
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to resume playback");
			}
		});
	}

	@Override
	public void pause() {
		execute(() -> {
			try {
				mBinding.pause();
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to pause playback");
			}
		});
	}

	@Override
	public void updatePlayerPreferences(ReadOnlyPreferenceStore preferenceStore) {
		execute(() -> {
			try {
				mBinding.setPreferences(new ImmutablePreferenceStore(preferenceStore));
				mShuffled.onNext(preferenceStore.isShuffled());
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to update remote player preferences");
			}
		});
	}

	@Override
	public void setQueue(List<Song> newQueue, int newPosition) {
		execute(() -> {
			try {
				mBinding.setQueue(newQueue, newPosition);
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to set queue");
			}
		});
	}

	@Override
	public void clearQueue() {
		setQueue(Collections.emptyList(), 0);
	}

	@Override
	public void changeSong(int newPosition) {
		execute(() -> {
			try {
				mBinding.changeSong(newPosition);
				mNowPlaying.invalidate();
				mQueuePosition.invalidate();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to change song");
			}
		});
	}

	@Override
	public void editQueue(List<Song> queue, int newPosition) {
		execute(() -> {
			try {
				mBinding.editQueue(queue, newPosition);
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to edit queue");
			}
		});
	}

	@Override
	public void queueNext(Song song) {
		execute(() -> {
			try {
				mBinding.queueNext(song);
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to queue next song");
			}
		});
	}

	@Override
	public void queueNext(List<Song> songs) {
		execute(() -> {
			try {
				mBinding.queueNextList(songs);
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to queue next songs");
			}
		});
	}

	@Override
	public void queueLast(Song song) {
		execute(() -> {
			try {
				mBinding.queueLast(song);
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to queue last song");
			}
		});
	}

	@Override
	public void queueLast(List<Song> songs) {
		execute(() -> {
			try {
				mBinding.queueLastList(songs);
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to queue last songs");
			}
		});
	}

	@Override
	public void seek(int position) {
		execute(() -> {
			try {
				mBinding.seekTo(position);
				invalidateAll();
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to seek");
			}
		});
	}

	@Override
	public Observable<Boolean> isPlaying() {
		ensureServiceStarted();
		return mPlaying.getObservable();
	}

	@Override
	public Observable<Song> getNowPlaying() {
		ensureServiceStarted();
		return mNowPlaying.getObservable();
	}

	@Override
	public Observable<List<Song>> getQueue() {
		ensureServiceStarted();
		return mQueue.getObservable();
	}

	@Override
	public Observable<Integer> getQueuePosition() {
		ensureServiceStarted();
		return mQueuePosition.getObservable();
	}

	@Override
	public Observable<Integer> getCurrentPosition() {
		ensureServiceStarted();
		startCurrentPositionClock();
		return mCurrentPosition.getObservable();
	}

	@Override
	public Observable<Integer> getDuration() {
		ensureServiceStarted();
		return mDuration.getObservable();
	}

	@Override
	public Observable<Boolean> isShuffleEnabled() {
		ensureServiceStarted();
		return mShuffled.distinctUntilChanged();
	}

	@Override
	public Observable<Integer> getMultiRepeatCount() {
		ensureServiceStarted();
		return mMultiRepeatCount.getObservable();
	}

	@Override
	public void setMultiRepeatCount(int count) {
		execute(() -> {
			try {
				mBinding.setMultiRepeatCount(count);
				mMultiRepeatCount.setValue(count);
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to set multi-repeat count");
			}
		});
	}

	@Override
	public Observable<Long> getSleepTimerEndTime() {
		ensureServiceStarted();
		return mSleepTimerEndTime.getObservable();
	}

	@Override
	public void setSleepTimerEndTime(long timestampInMillis) {
		execute(() -> {
			try {
				mBinding.setSleepTimerEndTime(timestampInMillis);
				mSleepTimerEndTime.setValue(timestampInMillis);
			} catch (RemoteException exception) {
				Timber.e(exception, "Failed to set sleep-timer end time");
			}
		});
	}

	@Override
	public void disableSleepTimer() {
		setSleepTimerEndTime(0L);
	}

	@Override
	public Observable<Bitmap> getArtwork() {
		if (mArtwork == null) {
			mArtwork = BehaviorSubject.create();

			getNowPlaying()
					.observeOn(Schedulers.io())
					.map((Song song) -> {
						if (song == null) {
							return null;
						}
						if (!song.isInLibrary()) {
							Bitmap bitmap = Glide.with(mContext)
									.asBitmap()
									.load(song.getArtWork()).into(100, 100).get();
							return bitmap;
						}

						return Util.fetchFullArt(mContext, song);
					})
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(mArtwork::onNext, throwable -> {
						Timber.e(throwable, "Failed to fetch artwork");
						mArtwork.onNext(null);
					});
		}

		return mNowPlaying.getSubject()
				.map(Optional::isPresent)
				.switchMap(current -> {
					if (current) {
						return mArtwork;
					} else {
						return Observable.empty();
					}
				});
	}

	/**
	 * A {@link BroadcastReceiver} class listening for intents with an
	 * {@link MusicPlayer#UPDATE_BROADCAST} action. This broadcast must be sent ordered with this
	 * receiver being the highest priority so that the UI can access this class for accurate
	 * information from the player service
	 */
	public static class Listener extends BroadcastReceiver {

		@Inject
		PlayerController mController;

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra(MusicPlayer.UPDATE_EXTRA_MINOR, false)) {
				// Ignore minor updates – we already handle them without being notified
				return;
			}

			if (mController == null) {
				SargamApplication.getComponent(context).inject(this);
			}

			if (mController instanceof ServicePlayerController) {
				ServicePlayerController playerController = (ServicePlayerController) mController;

				if (intent.getAction().equals(MusicPlayer.UPDATE_BROADCAST)) {
					playerController.invalidateAll();
				} else if (intent.getAction().equals(MusicPlayer.INFO_BROADCAST)) {
					String error = intent.getExtras().getString(MusicPlayer.INFO_EXTRA_MESSAGE);
					playerController.mInfoStream.onNext(error);

				} else if (intent.getAction().equals(MusicPlayer.ERROR_BROADCAST)) {
					String info = intent.getExtras().getString(MusicPlayer.ERROR_EXTRA_MSG);
					playerController.mErrorStream.onNext(info);
				}
			}
		}

	}

	private static final class Prop<T> {

		private final String mName;
		private final T mNullValue;
		private final BehaviorSubject<Optional<T>> mSubject;
		private final Observable<T> mObservable;

		private Retriever<T> mRetriever;

		public Prop(String propertyName) {
			this(propertyName, null);
		}

		public Prop(String propertyName, T nullValue) {
			mName = propertyName;
			mNullValue = nullValue;
			mSubject = BehaviorSubject.create();

			mObservable = mSubject.filter(Optional::isPresent)
					.map(Optional::getValue)
					.distinctUntilChanged();
		}

		public void setFunction(Retriever<T> retriever) {
			mRetriever = retriever;
		}

		public void invalidate() {
			mSubject.onNext(Optional.empty());

			if (mRetriever != null) {
				Observable.fromCallable(mRetriever::retrieve)
						.map(data -> (data == null) ? mNullValue : data)
						.map(Optional::ofNullable)
						.subscribe(mSubject::onNext, throwable -> {
//							Timber.e(throwable, "Failed to fetch " + mName + " property.");
						});
			}
		}

		public boolean isSubscribedTo() {
			return mSubject.hasObservers();
		}

		public void setValue(T value) {
			mSubject.onNext(Optional.ofNullable(value));
		}

		protected BehaviorSubject<Optional<T>> getSubject() {
			return mSubject;
		}

		public Observable<T> getObservable() {
			return mObservable;
		}

		interface Retriever<T> {
			T retrieve() throws Exception;
		}

	}
}
