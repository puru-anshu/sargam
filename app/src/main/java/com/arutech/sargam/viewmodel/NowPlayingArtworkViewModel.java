package com.arutech.sargam.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.arutech.sargam.BR;
import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.activity.BaseActivity;
import com.arutech.sargam.data.store.PreferenceStore;
import com.arutech.sargam.fragments.BaseFragment;
import com.arutech.sargam.player.MusicPlayer;
import com.arutech.sargam.player.PlayerController;
import com.arutech.sargam.utils.Util;
import com.arutech.sargam.view.GestureView;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import timber.log.Timber;


public class NowPlayingArtworkViewModel extends BaseObservable {

	@Inject
	PreferenceStore mPrefStore;
	@Inject
	PlayerController mPlayerController;

	private Context mContext;
	private Bitmap mArtwork;
	private boolean mPlaying;

	public NowPlayingArtworkViewModel(BaseActivity activity) {
		this(activity, activity.bindToLifecycle());
	}

	public NowPlayingArtworkViewModel(BaseFragment fragment) {
		this(fragment.getContext(), fragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
	}

	@SuppressWarnings("unchecked")
	private NowPlayingArtworkViewModel(Context context, LifecycleTransformer<?> transformer) {
		mContext = context;
		SargamApplication.getComponent(context).inject(this);

		mPlayerController.getArtwork()
				.compose((LifecycleTransformer<Bitmap>) transformer)
				.subscribe(this::setArtwork,
						throwable -> Timber.e(throwable, "Failed to set artwork"));

		mPlayerController.isPlaying()
				.compose((LifecycleTransformer<Boolean>) transformer)
				.subscribe(this::setPlaying,
						throwable -> Timber.e(throwable, "Failed to update playing state"));
	}

	private void setPlaying(boolean playing) {
		mPlaying = playing;
		notifyPropertyChanged(BR.tapIndicator);
	}

	public int getPortraitArtworkHeight() {
		// Only used when in portrait orientation
		int reservedHeight = (int) mContext.getResources().getDimension(R.dimen.player_frame_peek);

		// Default to a square view, so set the height equal to the width
		//noinspection SuspiciousNameCombination
		int preferredHeight = mContext.getResources().getDisplayMetrics().widthPixels;
		int maxHeight = mContext.getResources().getDisplayMetrics().heightPixels - reservedHeight;

		return Math.min(preferredHeight, maxHeight);
	}

	private void setArtwork(Bitmap artwork) {
		mArtwork = artwork;
		notifyPropertyChanged(BR.nowPlayingArtwork);
	}

	@Bindable
	public Drawable getNowPlayingArtwork() {
		if (mArtwork == null) {
			return ContextCompat.getDrawable(mContext, R.drawable.ic_empty_music2);
		} else {
			return new BitmapDrawable(mContext.getResources(), mArtwork);
		}
	}

	@Bindable
	public Drawable getNowPlayingArtworkBlurred() {

		if (mArtwork == null) {
			return ContextCompat.getDrawable(mContext, R.drawable.ic_empty_music2);
		} else {
			return new BitmapDrawable(mContext.getResources(), Util.fastblur(mArtwork, 1, 4));
		}


	}

	@Bindable
	public boolean getGesturesEnabled() {
		return mPrefStore.enableNowPlayingGestures();
	}

	@Bindable
	public Drawable getTapIndicator() {
		return ContextCompat.getDrawable(mContext,
				mPlaying
						? R.drawable.ic_pause_36dp
						: R.drawable.ic_play_arrow_36dp);
	}

	public GestureView.OnGestureListener getGestureListener() {
		return new GestureView.OnGestureListener() {
			@Override
			public void onLeftSwipe() {
				mPlayerController.skip();
			}

			@Override
			public void onRightSwipe() {
				mPlayerController.getQueuePosition()
						.take(1)
						.flatMap((queuePosition) -> {
							// Wrap to end of the queue when repeat all is enabled
							if (queuePosition == 0
									&& mPrefStore.getRepeatMode() == MusicPlayer.REPEAT_ALL) {
								return mPlayerController.getQueue()
										.take(1)
										.map(List::size)
										.map(size -> size - 1);
							} else {
								return Observable.just(queuePosition - 1);
							}
						})
						.subscribe((queuePosition) -> {
							if (queuePosition >= 0) {
								mPlayerController.changeSong(queuePosition);
							} else {
								mPlayerController.seek(0);
							}
						}, throwable -> {
							Timber.e(throwable, "Failed to handle skip gesture");
						});
			}

			@Override
			public void onTap() {
				mPlayerController.togglePlay();
				notifyPropertyChanged(BR.tapIndicator);
			}
		};
	}

}
