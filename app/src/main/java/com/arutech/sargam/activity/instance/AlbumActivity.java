package com.arutech.sargam.activity.instance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.activity.BaseLibraryActivity;
import com.arutech.sargam.adapter.HeterogeneousAdapter;
import com.arutech.sargam.adapter.LibraryEmptyState;
import com.arutech.sargam.adapter.SongSection;
import com.arutech.sargam.data.store.MusicStore;
import com.arutech.sargam.model.Album;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.view.BackgroundDecoration;
import com.arutech.sargam.view.DividerDecoration;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class AlbumActivity extends BaseLibraryActivity {

    private static final String ALBUM_EXTRA = "AlbumActivity.ALBUM";

    @Inject
    MusicStore mMusicStore;

    private HeterogeneousAdapter mAdapter;
    private SongSection mSongSection;
    private List<Song> mSongs;

    public static Intent newIntent(Context context, Album album) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra(ALBUM_EXTRA, album);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SargamApplication.getComponent(this).inject(this);

        Album reference = getIntent().getParcelableExtra(ALBUM_EXTRA);

        if (reference != null) {
            mMusicStore.getSongs(reference)
                    .compose(bindToLifecycle())
                    .subscribe(
                            songs -> {
                                mSongs = new ArrayList<>(songs);
                                Collections.sort(mSongs, Song.TRACK_COMPARATOR);
                                setupAdapter();
                            }, throwable -> {
                                Timber.e(throwable, "Failed to get song contents");
                            });

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(reference.getAlbumName());
            }

            Glide.with(this).load(reference.getArtUri())
                    .centerCrop()
                    .into((ImageView) findViewById(R.id.activity_backdrop));
        } else {
            mSongs = Collections.emptyList();
        }

        ImageView artistImage = (ImageView) findViewById(R.id.activity_backdrop);
        artistImage.getLayoutParams().height = calculateHeroHeight();

        mAdapter = new HeterogeneousAdapter();
        setupAdapter();
        mAdapter.setEmptyState(new LibraryEmptyState(this) {
            @Override
            public String getEmptyMessage() {
                if (reference == null) {
                    return getString(R.string.empty_error_album);
                } else {
                    return super.getEmptyMessage();
                }
            }

            @Override
            public String getEmptyMessageDetail() {
                if (reference == null) {
                    return "";
                } else {
                    return super.getEmptyMessageDetail();
                }
            }

            @Override
            public String getEmptyAction1Label() {
                return "";
            }
        });

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setAdapter(mAdapter);
        list.addItemDecoration(new BackgroundDecoration());
        list.addItemDecoration(new DividerDecoration(this, R.id.empty_layout));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
    }

    @Override
    protected int getContentLayoutResource() {
        return R.layout.activity_instance_artwork;
    }

    @Override
    public boolean isToolbarCollapsing() {
        return true;
    }

    private int calculateHeroHeight() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        int maxHeight = screenHeight / 2;

        // prefer a 1:1 aspect ratio
        return Math.min(screenWidth, maxHeight);
    }

    private void setupAdapter() {
        if (mAdapter == null || mSongs == null) {
            return;
        }

        if (mSongSection != null) {
            mSongSection.setData(mSongs);
            mAdapter.notifyDataSetChanged();
        } else {
            mSongSection = new SongSection(this, mSongs);
            mAdapter.addSection(mSongSection);
        }
    }
}
