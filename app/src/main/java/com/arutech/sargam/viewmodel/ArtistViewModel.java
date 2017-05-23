package com.arutech.sargam.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.activity.instance.ArtistActivity;
import com.arutech.sargam.data.store.MusicStore;
import com.arutech.sargam.dialog.AppendPlaylistDialogFragment;
import com.arutech.sargam.model.Artist;
import com.arutech.sargam.player.PlayerController;
import com.arutech.sargam.utils.Util;

import javax.inject.Inject;

import timber.log.Timber;

public class ArtistViewModel extends BaseObservable {

	private static final String TAG_PLAYLIST_DIALOG = "SongViewModel.PlaylistDialog";

	@Inject
	MusicStore mMusicStore;
	@Inject
	PlayerController mPlayerController;

	private Context mContext;
	private FragmentManager mFragmentManager;
	private Artist mArtist;
	private long songCount;
	private long albumCount;

	public ArtistViewModel(Context context, FragmentManager fragmentManager) {
		mContext = context;
		mFragmentManager = fragmentManager;

		SargamApplication.getComponent(context).inject(this);
	}

	public void setArtist(Artist artist) {
		mArtist = artist;
		mMusicStore.getAlbums(mArtist).count().subscribe(ct -> {
			this.albumCount = ct;
		}, throwable -> {
		});
		mMusicStore.getSongs(mArtist).count().subscribe(ct -> {
			this.songCount = ct;
		}, throwable -> {
		});
		notifyChange();
	}

	public String getName() {
		return mArtist.getArtistName();
	}

	@Bindable
	public String getSoungCount() {
		String albumNmber = Util.makeLabel(mContext, R.plurals.Nalbums, (int) albumCount);
		String songCountStr = Util.makeLabel(mContext, R.plurals.Nsongs, (int) songCount);
		return Util.makeCombinedString(mContext, albumNmber, songCountStr);
	}

	public View.OnClickListener onClickArtist() {
		return v -> mContext.startActivity(ArtistActivity.newIntent(mContext, mArtist));
	}

	public View.OnClickListener onClickMenu() {
		return v -> {
			PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
			menu.inflate(R.menu.instance_artist);
			menu.setOnMenuItemClickListener(onMenuItemClick());
			menu.show();
		};
	}

	private PopupMenu.OnMenuItemClickListener onMenuItemClick() {
		return menuItem -> {
			switch (menuItem.getItemId()) {
				case R.id.menu_item_queue_item_next:
					mMusicStore.getSongs(mArtist).subscribe(
							mPlayerController::queueNext,
							throwable -> {
								Timber.e(throwable, "Failed to get songs");
							});

					return true;
				case R.id.menu_item_queue_item_last:
					mMusicStore.getSongs(mArtist).subscribe(
							mPlayerController::queueLast,
							throwable -> {
								Timber.e(throwable, "Failed to get songs");
							});

					return true;
				case R.id.menu_item_add_to_playlist:
					mMusicStore.getSongs(mArtist).subscribe(
							songs -> {
								new AppendPlaylistDialogFragment.Builder(mContext, mFragmentManager)
										.setSongs(songs, mArtist.getArtistName())
										.showSnackbarIn(R.id.list)
										.show(TAG_PLAYLIST_DIALOG);
							}, throwable -> {
								Timber.e(throwable, "Failed to get songs");
							});

					return true;
			}
			return false;
		};
	}

}
