package com.arutech.sargam.viewmodel;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.activity.instance.AutoPlaylistActivity;
import com.arutech.sargam.activity.instance.AutoPlaylistEditActivity;
import com.arutech.sargam.activity.instance.PlaylistActivity;
import com.arutech.sargam.data.store.PlaylistStore;
import com.arutech.sargam.model.AutoPlaylist;
import com.arutech.sargam.model.Playlist;
import com.arutech.sargam.player.PlayerController;
import com.arutech.sargam.utils.Util;
import com.arutech.sargam.utils.ViewUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PlaylistViewModel extends BaseObservable {

	@Inject
	PlaylistStore mPlaylistStore;
	@Inject
	PlayerController mPlayerController;

	private Context mContext;
	private Playlist mPlaylist;
	private String numSong;
	private ObservableField<Drawable> mArtistImage;
	private ObservableInt mTitleTextColor;
	private ObservableInt mSubTitleTextColor;
	private ObservableInt mBackgroundColor;

	public PlaylistViewModel(Context context) {
		mContext = context;
		SargamApplication.getComponent(mContext).inject(this);
	}


	public void setPlaylist(Playlist playlist) {
		mPlaylist = playlist;
		mArtistImage = new ObservableField<>();
		mTitleTextColor = new ObservableInt();
		mSubTitleTextColor = new ObservableInt();
		mBackgroundColor = new ObservableInt();

		defaultColors(mContext, mTitleTextColor, mSubTitleTextColor, mBackgroundColor);


		mPlaylistStore.getSongs(playlist).subscribe(songs -> {
			if (songs.size() > 0) {
				numSong = Util.makeLabel(mContext, R.plurals.Nsongs, songs.size());
				long albumId = songs.get(0).getAlbumId();
				int imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.grid_width);
				Glide.with(mContext)
						.load(Util.getAlbumArtUri(albumId))
						.placeholder(R.drawable.ic_empty_music2)
						.error(R.drawable.ic_empty_music2)
						.listener(new PaletteListener(mTitleTextColor, mSubTitleTextColor, mBackgroundColor))
						.into(new ObservableTarget(imageSize, mArtistImage));

			} else {
				Drawable fallback = ResourcesCompat.getDrawable(mContext.getResources(),
						R.drawable.ic_empty_music2, mContext.getTheme());

				mArtistImage.set(fallback);
			}
		}, throwable -> {
		});

		notifyChange();
	}

	private static void defaultColors(Context context, ObservableInt title, ObservableInt artist,
	                                  ObservableInt background) {

		Resources res = context.getResources();
		Resources.Theme theme = context.getTheme();

		title.set(ResourcesCompat.getColor(res, R.color.grid_text, theme));
		artist.set(ResourcesCompat.getColor(res, R.color.grid_detail_text, theme));
		background.set(ResourcesCompat.getColor(res, R.color.blue_light, theme));
	}

	public ObservableInt getTitleTextColor() {
		return mTitleTextColor;
	}

	public ObservableInt getSubTitleTextColor() {
		return mSubTitleTextColor;
	}

	public ObservableInt getBackgroundColor() {
		return mBackgroundColor;
	}

	public ObservableField<Drawable> getArtistImage() {
		return mArtistImage;
	}

	public String getName() {
		return mPlaylist.getPlaylistName();
	}

	public int getSmartIndicatorVisibility() {
		if (mPlaylist instanceof AutoPlaylist) {
			return VISIBLE;
		} else {
			return GONE;
		}
	}

	public String getNumSong() {
		return numSong;
	}

	public View.OnClickListener onClickPlaylist() {
		return v -> {
			Intent intent;
			if (mPlaylist instanceof AutoPlaylist) {
				intent = AutoPlaylistActivity.newIntent(mContext, (AutoPlaylist) mPlaylist);
			} else {
				intent = PlaylistActivity.newIntent(mContext, mPlaylist);
			}
			mContext.startActivity(intent);
		};
	}

	public View.OnClickListener onClickMenu() {
		return v -> {
			PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
			menu.inflate((mPlaylist instanceof AutoPlaylist)
					? R.menu.instance_smart_playlist
					: R.menu.instance_playlist);

			menu.setOnMenuItemClickListener(onMenuItemClick(v));
			menu.show();
		};
	}

	private PopupMenu.OnMenuItemClickListener onMenuItemClick(View view) {
		return menuItem -> {
			switch (menuItem.getItemId()) {
				case R.id.menu_item_queue_item_next:
					queuePlaylistNext();
					return true;
				case R.id.menu_item_queue_item_last:
					queuePlaylistLast();
					return true;
				case R.id.menu_item_edit:
					editThisAsAutoPlaylist();
					return true;
				case R.id.menu_item_delete:
					if (mPlaylist instanceof AutoPlaylist) {
						deleteAutoPlaylist(view);
					} else {
						deletePlaylist(view);
					}
					return true;
			}
			return false;
		};
	}

	private void queuePlaylistNext() {
		mPlaylistStore.getSongs(mPlaylist).subscribe(
				mPlayerController::queueNext,
				throwable -> {
					Timber.e(throwable, "Failed to get songs");
				});
	}

	private void queuePlaylistLast() {
		mPlaylistStore.getSongs(mPlaylist).subscribe(
				mPlayerController::queueLast,
				throwable -> {
					Timber.e(throwable, "Failed to get songs");
				});
	}

	private void editThisAsAutoPlaylist() {
		AutoPlaylist autoPlaylist = (AutoPlaylist) mPlaylist;
		Intent intent = AutoPlaylistEditActivity.newIntent(mContext, autoPlaylist);
		mContext.startActivity(intent);
	}

	private void deletePlaylist(View snackbarContainer) {
		Playlist removed = mPlaylist;
		String playlistName = mPlaylist.getPlaylistName();
		String message = mContext.getString(R.string.message_removed_playlist, playlistName);

		mPlaylistStore.getSongs(removed)
				.subscribe(originalContents -> {
					mPlaylistStore.removePlaylist(removed);

					Snackbar.make(snackbarContainer, message, LENGTH_LONG)
							.setAction(R.string.action_undo, view -> {
								mPlaylistStore.makePlaylist(playlistName, originalContents);
							})
							.show();
				}, throwable -> {
					Timber.e(throwable, "Failed to get playlist contents");

					// If we can't get the original contents of the playlist, remove it anyway but
					// don't give an undo option
					mPlaylistStore.removePlaylist(removed);
					Snackbar.make(snackbarContainer, message, LENGTH_LONG).show();
				});
	}

	private void deleteAutoPlaylist(View snackbarContainer) {
		mPlaylistStore.removePlaylist(mPlaylist);

		String playlistName = mPlaylist.getPlaylistName();
		String message = mContext.getString(R.string.message_removed_playlist, playlistName);
		AutoPlaylist removed = (AutoPlaylist) mPlaylist;

		Snackbar.make(snackbarContainer, message, LENGTH_LONG)
				.setAction(R.string.action_undo, view -> {
					mPlaylistStore.makePlaylist(removed);
				})
				.show();
	}


	private static class ObservableTarget extends SimpleTarget<Drawable> {

		private ObservableField<Drawable> mTarget;

		public ObservableTarget(int size, ObservableField<Drawable> target) {
			super(size, size);
			mTarget = target;
		}

		@Override
		public void onLoadStarted(Drawable placeholder) {
			mTarget.set(placeholder);
		}

		@Override
		public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
			Drawable start = mTarget.get();

			if (start != null) {
				setDrawableWithFade(start, resource);
			} else {
				setDrawable(resource);
			}
		}


		private void setDrawableWithFade(Drawable start, Drawable end) {
			TransitionDrawable transition = new TransitionDrawable(new Drawable[]{start, end});
			transition.setCrossFadeEnabled(true);
			transition.startTransition(300);

			setDrawable(transition);
		}

		private void setDrawable(Drawable resource) {
			mTarget.set(resource);
		}
	}

	private static class PaletteListener implements RequestListener<Drawable> {

		private static Map<Uri, Palette.Swatch> sColorMap;

		private ObservableInt mTitleTextColor;
		private ObservableInt mArtistTextColor;
		private ObservableInt mBackgroundColor;

		static {
			sColorMap = new HashMap<>();
		}

		public PaletteListener(ObservableInt title, ObservableInt artist,
		                       ObservableInt background) {
			mTitleTextColor = title;
			mArtistTextColor = artist;
			mBackgroundColor = background;
		}

		private void generateSwatch(Uri source, Drawable image) {
			Palette.from(ViewUtils.drawableToBitmap(image)).generate(palette -> {
				Palette.Swatch swatch = pickSwatch(palette);

				sColorMap.put(source, swatch);
				animateSwatch(swatch);
			});
		}

		private void setSwatch(Palette.Swatch swatch) {
			if (swatch == null) {
				return;
			}

			mBackgroundColor.set(swatch.getRgb());
			mTitleTextColor.set(swatch.getTitleTextColor());
			mArtistTextColor.set(swatch.getBodyTextColor());
		}

		private void animateSwatch(Palette.Swatch swatch) {
			if (swatch == null) {
				return;
			}

			animateColorValue(mBackgroundColor, swatch.getRgb());
			animateColorValue(mTitleTextColor, swatch.getTitleTextColor());
			animateColorValue(mArtistTextColor, swatch.getBodyTextColor());
		}

		private void animateColorValue(ObservableInt target, @ColorInt int toColor) {
			ObjectAnimator.ofObject(target, "", new ArgbEvaluator(), target.get(), toColor)
					.setDuration(300)
					.start();
		}

		private Palette.Swatch pickSwatch(Palette palette) {
			if (palette.getVibrantSwatch() != null) {
				return palette.getVibrantSwatch();
			}
			if (palette.getLightVibrantSwatch() != null) {
				return palette.getLightVibrantSwatch();
			}
			if (palette.getDarkVibrantSwatch() != null) {
				return palette.getDarkVibrantSwatch();
			}
			if (palette.getLightMutedSwatch() != null) {
				return palette.getLightMutedSwatch();
			}
			if (palette.getDarkMutedSwatch() != null) {
				return palette.getDarkMutedSwatch();
			}
			return null;
		}


		@Override
		public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
			return false;
		}

		@Override
		public boolean onResourceReady(Drawable resource, Object model,
		                               Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
			return false;
		}
	}

}
