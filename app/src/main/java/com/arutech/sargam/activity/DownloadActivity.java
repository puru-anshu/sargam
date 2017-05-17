package com.arutech.sargam.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.adapter.BasicEmptyState;
import com.arutech.sargam.adapter.HeterogeneousAdapter;
import com.arutech.sargam.saavn.data.store.SaavnStore;
import com.arutech.sargam.utils.ViewUtils;
import com.arutech.sargam.view.BackgroundDecoration;
import com.arutech.sargam.view.DividerDecoration;

import javax.inject.Inject;

public class DownloadActivity extends BaseLibraryActivity {


	public static Intent newIntent(Context context) {
		return new Intent(context, DownloadActivity.class);
	}

	@Inject
	SaavnStore mSaavnStore;
	private RecyclerView mRecyclerView;
	private HeterogeneousAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SargamApplication.getComponent(this).inject(this);
		mRecyclerView = (RecyclerView) findViewById(R.id.list);
		initAdapter();
	}

	private void initAdapter() {
		mAdapter = new HeterogeneousAdapter();
//				.addSection(new HeaderSection(getString(R.string.header_playlists)))
//				.addSection(mPlaylistSection)
//				.addSection(new HeaderSection(getString(R.string.header_songs)))
//				.addSection(mSongSection)
//				.addSection(new HeaderSection(getString(R.string.header_albums)))
//				.addSection(mAlbumSection)
//				.addSection(new HeaderSection(getString(R.string.header_artists)))
//				.addSection(mArtistSection)
//				.addSection(new HeaderSection(getString(R.string.header_genres)))
//				.addSection(mGenreSection);

		mAdapter.setEmptyState(new BasicEmptyState() {
			@Override
			public String getMessage() {
				return  getString(R.string.empty_search);
			}
		});

		mRecyclerView.setAdapter(mAdapter);

		final int numColumns = ViewUtils.getNumberOfGridColumns(this, R.dimen.grid_width);

		GridLayoutManager layoutManager = new GridLayoutManager(this, numColumns);
//		layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//			@Override
//			public int getSpanSize(int position) {
//				if (mAdapter.getItemViewType(position) == mAlbumSection.getTypeId()) {
//					return 1;
//				}
//				return numColumns;
//			}
//		});
		mRecyclerView.setLayoutManager(layoutManager);

		// Add item decorations
//		mRecyclerView.addItemDecoration(new GridSpacingDecoration(
//				(int) getResources().getDimension(R.dimen.grid_margin),
//				numColumns, mAlbumSection.getTypeId()));
		mRecyclerView.addItemDecoration(
				new BackgroundDecoration(R.id.subheader_frame));
		mRecyclerView.addItemDecoration(
				new DividerDecoration(this,
						R.id.album_view, R.id.subheader_frame, R.id.empty_layout));
	}

	@Override
	protected int getContentLayoutResource() {
		return R.layout.activity_instance;
	}
}
