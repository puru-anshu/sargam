package com.arutech.sargam.viewmodel.web;

import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;

import com.arutech.sargam.BR;
import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.activity.BaseActivity;
import com.arutech.sargam.activity.BaseLibraryActivity;
import com.arutech.sargam.data.store.PreferenceStore;
import com.arutech.sargam.fragments.BaseFragment;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.player.PlayerController;
import com.arutech.sargam.saavn.data.store.SaavnStore;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class WSongViewModel extends BaseObservable {

	private static final String TAG_PLAYLIST_DIALOG = "WSongViewModel.PlaylistDialog";

	@Inject
	SaavnStore mSaavnStore;
	@Inject
	PreferenceStore mPrefStore;
	@Inject
	PlayerController mPlayerController;

	private Context mContext;
	private Activity mActivity;
	private FragmentManager mFragmentManager;
	private LifecycleTransformer<?> mLifecycleTransformer;
	private Disposable mNowPlayingSubscription;

	private List<Song> mSongList;
	private int mIndex;
	private boolean mIsPlaying;
	private Song mReference;
	private ObservableField<Drawable> mAlbumArt;

	public WSongViewModel(BaseActivity activity, List<Song> songs) {
		this(activity, activity.getSupportFragmentManager(),
				activity.bindUntilEvent(ActivityEvent.DESTROY), songs);
	}

	public WSongViewModel(BaseFragment fragment, List<Song> songs) {
		this(fragment.getActivity(), fragment.getFragmentManager(),
				fragment.bindUntilEvent(FragmentEvent.DESTROY), songs);
	}

	public WSongViewModel(Activity activity, FragmentManager fragmentManager,
	                      LifecycleTransformer<?> lifecycleTransformer, List<Song> songs) {
		mContext = activity;
		mActivity = activity;
		mFragmentManager = fragmentManager;
		mLifecycleTransformer = lifecycleTransformer;
		mSongList = songs;

		SargamApplication.getComponent(mContext).inject(this);
	}

	public void setIndex(int index) {
		setSong(mSongList, index);
	}

	protected int getIndex() {
		return mIndex;
	}

	protected Song getReference() {
		return mReference;
	}

	protected List<Song> getSongs() {
		return mSongList;
	}

	private <T> LifecycleTransformer<T> bindToLifecycle() {
		//noinspection unchecked
		return (LifecycleTransformer<T>) mLifecycleTransformer;
	}

	protected Observable<Boolean> isPlaying() {
		return mPlayerController.getNowPlaying()
				.map(playing -> playing != null && playing.equals(getReference()));
	}

	public void setSong(List<Song> songList, int index) {
		mSongList = songList;
		mIndex = index;
		mReference = songList.get(index);

		if (mNowPlayingSubscription != null) {
			mNowPlayingSubscription.dispose();
		}

		mIsPlaying = false;
		mNowPlayingSubscription = isPlaying()
				.compose(bindToLifecycle())
				.subscribe(isPlaying -> {
					mIsPlaying = isPlaying;
					notifyPropertyChanged(BR.nowPlayingIndicatorVisibility);
				}, throwable -> {
					Timber.e(throwable, "Failed to update playing indicator");
				});

		notifyPropertyChanged(BR.title);
		notifyPropertyChanged(BR.detail);
	}

	@Bindable
	public int getNowPlayingIndicatorVisibility() {
		if (mIsPlaying) {
			return View.VISIBLE;
		} else {
			return View.GONE;
		}
	}

	@Bindable
	public String getTitle() {
		return mReference.getSongName();
	}

	@Bindable
	public String getDetail() {
		return mContext.getString(R.string.format_compact_song_info,
				mReference.getArtistName(), mReference.getAlbumName());
	}

	@Bindable
	public String getArtwork() {
		return mReference.getArtWork();
	}


	public View.OnClickListener onClickSong() {
		return v -> {
			mPlayerController.setQueue(mSongList, mIndex);
			mPlayerController.play();

			if (mPrefStore.openNowPlayingOnNewQueue() && mActivity instanceof BaseLibraryActivity) {
				((BaseLibraryActivity) mActivity).expandBottomSheet();
			}
		};
	}

	public View.OnClickListener onClickMenu() {
		return v -> {
			final PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
			menu.inflate(R.menu.instance_web_song);
			menu.setOnMenuItemClickListener(onMenuItemClick());
			menu.show();
		};
	}

	private PopupMenu.OnMenuItemClickListener onMenuItemClick() {
		return menuItem -> {
			switch (menuItem.getItemId()) {
				case R.id.menu_item_queue_item_next:
					mPlayerController.queueNext(mReference);
					return true;
				case R.id.menu_item_queue_item_last:
					mPlayerController.queueLast(mReference);
					return true;
				case R.id.menu_item_navigate_to_album:
					mSaavnStore.findAlbumById(mReference.getAlbumId()).subscribe(
							album -> {
								//@todo .. navigage to Album Activity
								//mContext.startActivity(AlbumActivity.newIntent(mContext, album));
							}, throwable -> {
								Timber.e(throwable, "Failed to find album", throwable);
							});
					return true;
				case R.id.menu_item_download_song:
					//@todo
					return true;
			}
			return false;
		};
	}

}
