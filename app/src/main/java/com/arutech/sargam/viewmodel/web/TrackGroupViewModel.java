package com.arutech.sargam.viewmodel.web;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.activity.instance.TrackGroupActivity;
import com.arutech.sargam.player.PlayerController;
import com.arutech.sargam.saavn.api.model.TrackGroup;
import com.arutech.sargam.saavn.data.store.SaavnStore;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class TrackGroupViewModel extends BaseObservable {


	@Inject
	SaavnStore mMusicStore;
	@Inject
	PlayerController mPlayerController;

	private Context mContext;
	private FragmentManager mFragmentManager;
	private TrackGroup mGroup;

	private ObservableField<Drawable> mArtistImage;
	private ObservableInt mTitleTextColor;
	private ObservableInt mArtistTextColor;
	private ObservableInt mBackgroundColor;

	public TrackGroupViewModel(Context context, FragmentManager fragmentManager) {
		mContext = context;
		mFragmentManager = fragmentManager;
		SargamApplication.getComponent(mContext).inject(this);
	}

	public void setTrackGroup(TrackGroup album) {
		this.mGroup = album;

		mArtistImage = new ObservableField<>();
		mTitleTextColor = new ObservableInt();
		mArtistTextColor = new ObservableInt();
		mBackgroundColor = new ObservableInt();

		defaultColors();

		if (mGroup.getImage() != null) {
			int imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.grid_width);

			Glide.with(mContext)
					.load(mGroup.getImage())
					.placeholder(R.drawable.ic_empty_music2)
					.error(R.drawable.ic_empty_music2)
					.listener(new PaletteListener(mTitleTextColor, mArtistTextColor,
							mBackgroundColor))
					.into(new ObservableTarget(imageSize, mArtistImage));
		} else {
			Drawable fallback = ResourcesCompat.getDrawable(mContext.getResources(),
					R.drawable.ic_empty_music2, mContext.getTheme());

			mArtistImage.set(fallback);
		}

		notifyChange();
	}

	private void defaultColors() {
		defaultColors(mContext, mTitleTextColor, mArtistTextColor, mBackgroundColor);
	}

	private static void defaultColors(Context context, ObservableInt title, ObservableInt artist,
	                                  ObservableInt background) {

		Resources res = context.getResources();
		Resources.Theme theme = context.getTheme();

		title.set(ResourcesCompat.getColor(res, R.color.grid_text, theme));
		artist.set(ResourcesCompat.getColor(res, R.color.grid_detail_text, theme));
		background.set(ResourcesCompat.getColor(res, R.color.grid_background_default, theme));
	}

	public String getAlbumTitle() {
		return mGroup.getTitle();
	}

	public String getAlbumArtist() {
		return mGroup.getDescription();
	}

	public ObservableField<Drawable> getArtistImage() {
		return mArtistImage;
	}

	public ObservableInt getTitleTextColor() {
		return mTitleTextColor;
	}

	public ObservableInt getArtistTextColor() {
		return mArtistTextColor;
	}

	public ObservableInt getBackgroundColor() {
		return mBackgroundColor;
	}

	public View.OnClickListener onClickGroup() {
		return view -> mContext.startActivity(
				TrackGroupActivity.newIntent(mContext, mGroup.getId(), mGroup.getType(), mGroup.getTitle()));

	}

	public View.OnLongClickListener onLongClick() {
		return new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				return true;
			}
		};
	}

	public View.OnClickListener onClickMenu() {
		return v -> {
			PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
			menu.inflate(R.menu.instance_track_group);
			menu.setOnMenuItemClickListener(onMenuItemClick());
			menu.show();
		};
	}

	private PopupMenu.OnMenuItemClickListener onMenuItemClick() {
		return menuItem -> {
			switch (menuItem.getItemId()) {
				case R.id.menu_item_queue_item_next:
					mMusicStore.getSongs(mGroup).observeOn(AndroidSchedulers.mainThread())
							.subscribe(
									mPlayerController::queueNext,
									throwable -> {
										Timber.e(throwable, "Failed to get songs");
									});

					return true;
				case R.id.menu_item_queue_item_last:
					mMusicStore.getSongs(mGroup).observeOn(AndroidSchedulers.mainThread())
							.subscribe(
									mPlayerController::queueLast,
									throwable -> {
										Timber.e(throwable, "Failed to get songs");
									});

					return true;

				case R.id.menu_item_add_to_playlist:
					mMusicStore.getSongs(mGroup).subscribe(
							songs -> {
								//@todo

							}, throwable -> {
								Timber.e(throwable, "Failed to get songs");
							});

					return true;
			}
			return false;
		};
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

		private static Map<String, Palette.Swatch> sColorMap;

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



		private void generateSwatch(String source, Drawable image) {
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
		public boolean onResourceReady(Drawable resource,
		                               Object model, Target<Drawable> target,
		                               DataSource dataSource, boolean isFirstResource) {
			if (sColorMap.containsKey(model.toString())) {
				Palette.Swatch swatch = sColorMap.get(model);
				if (isFirstResource) {
					setSwatch(swatch);
				} else {
					animateSwatch(swatch);
				}
			} else {
				generateSwatch(model.toString(), resource);
			}
			return false;
		}
	}
}
