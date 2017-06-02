package com.arutech.sargam.fragments.web;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.adapter.BasicEmptyState;
import com.arutech.sargam.adapter.HeterogeneousAdapter;
import com.arutech.sargam.adapter.HeterogeneousFastScrollAdapter;
import com.arutech.sargam.adapter.web.WSongSection;
import com.arutech.sargam.fragments.BaseFragment;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.saavn.data.store.SaavnStore;
import com.arutech.sargam.view.BackgroundDecoration;
import com.arutech.sargam.view.DividerDecoration;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class WebSongFragment extends BaseFragment {

	@Inject
	SaavnStore mMusicStore;

	private FastScrollRecyclerView mRecyclerView;
	private HeterogeneousAdapter mAdapter;
	private WSongSection mSongSection;
	private List<Song> mSongs;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SargamApplication.getComponent(this).inject(this);

		mMusicStore.getSongs()
				.compose(bindToLifecycle())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						songs -> {
							mSongs = songs;
							setupAdapter();
						},
						throwable -> Timber.e(throwable, "Failed to get new songs"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_library_page, container, false);
		mRecyclerView = (FastScrollRecyclerView) view.findViewById(R.id.library_page_list);
		mRecyclerView.addItemDecoration(new BackgroundDecoration());
		mRecyclerView.addItemDecoration(new DividerDecoration(getContext(), R.id.empty_layout));

		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);

		if (mAdapter == null) {
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
		mSongSection = null;
	}

	private void setupAdapter() {
		if (mRecyclerView == null || mSongs == null) {
			return;
		}

		if (mSongSection != null) {
			mSongSection.setData(mSongs);
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter = new HeterogeneousFastScrollAdapter();
			mAdapter.setHasStableIds(true);
			mRecyclerView.setAdapter(mAdapter);

			mSongSection = new WSongSection(this, mSongs);
			mAdapter.addSection(mSongSection);
			mAdapter.setEmptyState((new BasicEmptyState() {
				@Override
				public String getMessage() {
					return "";
				}
			}));
		}
	}
}
