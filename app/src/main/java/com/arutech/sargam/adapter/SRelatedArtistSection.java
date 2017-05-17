package com.arutech.sargam.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arutech.sargam.R;
import com.arutech.sargam.activity.instance.ArtistActivity;
import com.arutech.sargam.data.store.MusicStore;
import com.arutech.sargam.model.Artist;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import timber.log.Timber;

public class SRelatedArtistSection extends HeterogeneousAdapter.ListSection<com.arutech.sargam.saavn.api.model.Artist> {

    private MusicStore mMusicStore;

    public SRelatedArtistSection(MusicStore musicStore, @NonNull List<com.arutech.sargam.saavn.api.model.Artist> data) {
        super(data);
        mMusicStore = musicStore;
    }

    @Override
    public EnhancedViewHolder<com.arutech.sargam.saavn.api.model.Artist> createViewHolder(HeterogeneousAdapter adapter,
                                                          ViewGroup parent) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.instance_artist_suggested, parent, false));
    }

    @Override
    public int getId(int position) {
        return get(position).getId().hashCode();
    }

    private class ViewHolder extends EnhancedViewHolder<com.arutech.sargam.saavn.api.model.Artist>
            implements View.OnClickListener {

        private Artist localReference;

        private Context context;
        private ImageView artwork;
        private TextView artistName;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            itemView.setOnClickListener(this);

            artwork = (ImageView) itemView.findViewById(R.id.artist_suggested_artwork);
            artistName = (TextView) itemView.findViewById(R.id.artist_suggested_name);
        }

        @Override
        public void onUpdate(com.arutech.sargam.saavn.api.model.Artist item, int sectionPosition) {
            mMusicStore.findArtistByName(item.getName())
                    .take(1)
                    .subscribe(
                            artist -> {
                                localReference = artist;
                            }, throwable -> {
                                Timber.e(throwable, "Failed to get local reference");
                            });


            String artUrl = item.getImage();

            Glide.with(context)
                    .load(artUrl)
                    .asBitmap()
                    .error(R.drawable.ic_empty_music2)
                    .into(new BitmapImageViewTarget(artwork) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(
                                            context.getResources(),
                                            resource);
                            circularBitmapDrawable.setCircular(true);
                            artwork.setImageDrawable(circularBitmapDrawable);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(
                                            context.getResources(),
                                            ((BitmapDrawable) errorDrawable).getBitmap());
                            circularBitmapDrawable.setCircular(true);
                            artwork.setImageDrawable(circularBitmapDrawable);
                        }
                    });

            artistName.setText(item.getName());
        }

        @Override
        public void onClick(View v) {
            if (localReference != null) {
                context.startActivity(ArtistActivity.newIntent(context, localReference));
            }
        }
    }
}
