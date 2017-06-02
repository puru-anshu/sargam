package com.arutech.sargam.fragments.web;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.adapter.BasicEmptyState;
import com.arutech.sargam.adapter.HeterogeneousAdapter;
import com.arutech.sargam.adapter.HeterogeneousFastScrollAdapter;
import com.arutech.sargam.adapter.web.TrackGroupSection;
import com.arutech.sargam.fragments.BaseFragment;
import com.arutech.sargam.saavn.api.model.TrackGroup;
import com.arutech.sargam.saavn.data.store.SaavnStore;
import com.arutech.sargam.utils.ViewUtils;
import com.arutech.sargam.view.BackgroundDecoration;
import com.arutech.sargam.view.GridSpacingDecoration;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class TrackGroupFragment extends BaseFragment {

	public static final String TRACK_GROUP_TYPE = "track_group_type_key";

	public static TrackGroupFragment getInstance(String type) {
		TrackGroupFragment fragment = new TrackGroupFragment();
		Bundle args = new Bundle();
		args.putString(TRACK_GROUP_TYPE, type);
		fragment.setArguments(args);
		return fragment;
	}

	@Inject
	SaavnStore mMusicStore;


	private FastScrollRecyclerView mRecyclerView;
	private HeterogeneousAdapter mAdapter;
	private TrackGroupSection mGroupSection;
	private List<TrackGroup> mAlbums;
	private String type;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SargamApplication.getComponent(this).inject(this);
		String type = getArguments().getString(TRACK_GROUP_TYPE);


		Observable<List<TrackGroup>> listObservable = null;
		if (type.equals("playlist"))
			listObservable = mMusicStore.getPlaylist();
		else
			listObservable = mMusicStore.getAlbums();

		listObservable
				.compose(bindToLifecycle()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						albums -> {
							mAlbums = albums;
							setupAdapter();
						}, throwable -> {
							Timber.e(throwable, "Failed to get all albums from MusicStore");
						});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_library_page, container, false);
		mRecyclerView = (FastScrollRecyclerView) view.findViewById(R.id.library_page_list);

		int numColumns = ViewUtils.getNumberOfGridColumns(getActivity(), R.dimen.grid_width);

		GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return mAlbums.isEmpty() ? numColumns : 1;
			}
		});
		mRecyclerView.setLayoutManager(layoutManager);

		mRecyclerView.addItemDecoration(new BackgroundDecoration());
		mRecyclerView.addItemDecoration(new GridSpacingDecoration(
				(int) getResources().getDimension(R.dimen.grid_margin), numColumns));

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
		mGroupSection = null;
	}

	private void setupAdapter() {
		if (mRecyclerView == null || mAlbums == null) {
			return;
		}

		if (mGroupSection != null) {
			mGroupSection.setData(mAlbums);
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter = new HeterogeneousFastScrollAdapter();
			mAdapter.setHasStableIds(true);
			mRecyclerView.setAdapter(mAdapter);

			mGroupSection = new TrackGroupSection(this, mAlbums);
			mAdapter.addSection(mGroupSection);
			mAdapter.setEmptyState(new BasicEmptyState() {
				@Override
				public String getMessage() {
					return "";
				}
			});
		}
	}
}
