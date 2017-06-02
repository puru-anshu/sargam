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
import com.arutech.sargam.adapter.web.WSongSection;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.saavn.data.store.SaavnStore;
import com.arutech.sargam.utils.Util;
import com.arutech.sargam.view.BackgroundDecoration;
import com.arutech.sargam.view.DividerDecoration;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class TrackGroupActivity extends BaseLibraryActivity {

	private static final String TRACK_GROUP_ID = "TrackGroup.ID";
	private static final String TRACK_GROUP_TYPE = "TrackGroup.TYPE";
	private static final String TRACK_GROUP_Title = "TrackGroup.Title";

	@Inject
	SaavnStore mMusicStore;

	private HeterogeneousAdapter mAdapter;
	private WSongSection mSongSection;
	private List<Song> mSongs;

	public static Intent newIntent(Context context, String id, String type, String title) {
		Intent intent = new Intent(context, TrackGroupActivity.class);
		intent.putExtra(TRACK_GROUP_ID, id);
		intent.putExtra(TRACK_GROUP_TYPE, type);
		intent.putExtra(TRACK_GROUP_Title, title);

		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SargamApplication.getComponent(this).inject(this);

		String groupId = getIntent().getStringExtra(TRACK_GROUP_ID);
		String type = getIntent().getStringExtra(TRACK_GROUP_TYPE);
		String title = getIntent().getStringExtra(TRACK_GROUP_Title);

		if (groupId != null) {
			mMusicStore.getTrackGroup(groupId, type)
					.compose(bindToLifecycle())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(
							groupDetail -> {
								mSongs = Util.getSongsFromTracks(groupDetail.getList());
								setupAdapter();
								Glide.with(this).load(groupDetail.getImage())
										.centerCrop()
										.into((ImageView) findViewById(R.id.activity_backdrop));

							}, throwable -> {
								Timber.e(throwable, "Failed to get song contents");
							});

			if (getSupportActionBar() != null) {
				getSupportActionBar().setTitle(title);
			}

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
				return super.getEmptyMessage();

			}

			@Override
			public String getEmptyMessageDetail() {
				if (mSongs == null) {
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
			mSongSection = new WSongSection(this, mSongs);
			mAdapter.addSection(mSongSection);
		}
	}
}
