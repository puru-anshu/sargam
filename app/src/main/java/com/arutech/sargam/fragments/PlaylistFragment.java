package com.arutech.sargam.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.adapter.HeterogeneousAdapter;
import com.arutech.sargam.adapter.HeterogeneousFastScrollAdapter;
import com.arutech.sargam.adapter.LibraryEmptyState;
import com.arutech.sargam.adapter.PlaylistSection;
import com.arutech.sargam.adapter.SpacerSingleton;
import com.arutech.sargam.data.store.PlaylistStore;
import com.arutech.sargam.model.Playlist;
import com.arutech.sargam.utils.ViewUtils;
import com.arutech.sargam.view.BackgroundDecoration;
import com.arutech.sargam.view.DividerDecoration;
import com.arutech.sargam.view.GridSpacingDecoration;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class PlaylistFragment extends BaseFragment {

	@Inject
	PlaylistStore mPlaylistStore;

	private RecyclerView mRecyclerView;
	private HeterogeneousAdapter mAdapter;
	private PlaylistSection mPlaylistSection;
	private List<Playlist> mPlaylists;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SargamApplication.getComponent(this).inject(this);
		mPlaylistStore.getPlaylists()
				.compose(bindToLifecycle())
				.subscribe(
						playlists -> {
							mPlaylists = playlists;
							setupAdapter();
						}, throwable -> {
							Timber.e(throwable, "Failed to get all playlists from PlaylistStore");
						});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_library_page, container, false);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.library_page_list);
		int numColumns = ViewUtils.getNumberOfGridColumns(getActivity(), R.dimen.grid_width);

		GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return mPlaylists.isEmpty() ? numColumns : 1;
			}
		});
		mRecyclerView.setLayoutManager(layoutManager);

		mRecyclerView.addItemDecoration(new BackgroundDecoration());
		mRecyclerView.addItemDecoration(new GridSpacingDecoration(
				(int) getResources().getDimension(R.dimen.grid_margin), numColumns));
		mRecyclerView.addItemDecoration(new BackgroundDecoration());
		mRecyclerView.addItemDecoration(
				new DividerDecoration(getActivity(), R.id.instance_blank, R.id.empty_layout));

		if (mAdapter == null) {
			if (null == mPlaylists) mPlaylists = Collections.emptyList();
			setupAdapter();
		} else {
			mRecyclerView.setAdapter(mAdapter);
		}

		int paddingH = (int) getActivity().getResources().getDimension(R.dimen.global_padding);
		view.setPadding(paddingH, 0, paddingH, 0);

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mRecyclerView = null;
		mAdapter = null;
		mPlaylistSection = null;
	}

	private void setupAdapter() {
		if (mRecyclerView == null || mPlaylists == null) {
			return;
		}

		if (mPlaylistSection != null) {
			mPlaylistSection.setData(mPlaylists);
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter = new HeterogeneousFastScrollAdapter();
			mAdapter.setHasStableIds(true);
			mRecyclerView.setAdapter(mAdapter);

			mPlaylistSection = new PlaylistSection(mPlaylists);
			mAdapter.addSection(mPlaylistSection);
			mAdapter.addSection(new SpacerSingleton(
					(int) getResources().getDimension(R.dimen.list_height)));
			mAdapter.setEmptyState(new LibraryEmptyState(getActivity()) {
				@Override
				public String getEmptyMessage() {
					return getString(R.string.empty_playlists);
				}

				@Override
				public String getEmptyMessageDetail() {
					return getString(R.string.empty_playlists_detail);
				}

				@Override
				public String getEmptyAction1Label() {
					return "";
				}

				@Override
				public String getEmptyAction2Label() {
					return "";
				}
			});
		}
	}
}
