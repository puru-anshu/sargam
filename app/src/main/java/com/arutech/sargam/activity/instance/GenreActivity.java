package com.arutech.sargam.activity.instance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.activity.BaseLibraryActivity;
import com.arutech.sargam.adapter.HeterogeneousAdapter;
import com.arutech.sargam.adapter.LibraryEmptyState;
import com.arutech.sargam.adapter.SongSection;
import com.arutech.sargam.data.store.MusicStore;
import com.arutech.sargam.model.Genre;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.view.BackgroundDecoration;
import com.arutech.sargam.view.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class GenreActivity extends BaseLibraryActivity {

    private static final String GENRE_EXTRA = "GenreActivity.GENRE";

    @Inject
    MusicStore mMusicStore;

    private Genre reference;
    private List<Song> mSongs;
    private HeterogeneousAdapter mAdapter;
    private SongSection mSongSection;

    public static Intent newIntent(Context context, Genre genre) {
        Intent intent = new Intent(context, GenreActivity.class);
        intent.putExtra(GENRE_EXTRA, genre);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SargamApplication.getComponent(this).inject(this);

        reference = getIntent().getParcelableExtra(GENRE_EXTRA);

        if (reference != null) {
            mMusicStore.getSongs(reference)
                    .compose(bindToLifecycle())
                    .subscribe(
                            songs -> {
                                mSongs = songs;
                                setupAdapter();
                            }, throwable -> {
                                Timber.e(throwable, "Failed to get song contents");
                            });

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(reference.getGenreName());
            }
        } else {
            mSongs = new ArrayList<>();
        }

        mAdapter = new HeterogeneousAdapter();
        setupAdapter();
        mAdapter.setEmptyState(new LibraryEmptyState(this) {
            @Override
            public String getEmptyMessage() {
                if (reference == null) {
                    return getString(R.string.empty_error_genre);
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
        return R.layout.activity_instance;
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
