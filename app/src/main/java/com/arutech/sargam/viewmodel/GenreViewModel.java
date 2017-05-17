package com.arutech.sargam.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.activity.instance.GenreActivity;
import com.arutech.sargam.data.store.MusicStore;
import com.arutech.sargam.dialog.AppendPlaylistDialogFragment;
import com.arutech.sargam.model.Genre;
import com.arutech.sargam.player.PlayerController;

import javax.inject.Inject;

import timber.log.Timber;

public class GenreViewModel extends BaseObservable {

    private static final String TAG_PLAYLIST_DIALOG = "GenreViewModel.PlaylistDialog";

    @Inject
    MusicStore mMusicStore;
    @Inject
    PlayerController mPlayerController;

    private Context mContext;
    private FragmentManager mFragmentManager;
    private Genre mGenre;

    public GenreViewModel(Context context, FragmentManager fragmentManager) {
        mContext = context;
        mFragmentManager = fragmentManager;

        SargamApplication.getComponent(mContext).inject(this);
    }

    public void setGenre(Genre genre) {
        mGenre = genre;
        notifyChange();
    }

    public String getName() {
        return mGenre.getGenreName();
    }

    public View.OnClickListener onClickGenre() {
        return v -> mContext.startActivity(GenreActivity.newIntent(mContext, mGenre));
    }

    public View.OnClickListener onClickMenu() {
        return v -> {
            final PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
            menu.inflate(R.menu.instance_genre);
            menu.setOnMenuItemClickListener(onMenuItemClick());
            menu.show();
        };
    }

    private PopupMenu.OnMenuItemClickListener onMenuItemClick() {
        return menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_item_queue_item_next:
                    mMusicStore.getSongs(mGenre).subscribe(
                            mPlayerController::queueNext,
                            throwable -> {
                                Timber.e(throwable, "Failed to get songs");
                            });

                    return true;
                case R.id.menu_item_queue_item_last:
                    mMusicStore.getSongs(mGenre).subscribe(
                            mPlayerController::queueLast,
                            throwable -> {
                                Timber.e(throwable, "Failed to get songs");
                            });

                    return true;
                case R.id.menu_item_add_to_playlist:
                    mMusicStore.getSongs(mGenre).subscribe(
                            songs -> {
                                new AppendPlaylistDialogFragment.Builder(mContext, mFragmentManager)
                                        .setSongs(songs, mGenre.getGenreName())
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
