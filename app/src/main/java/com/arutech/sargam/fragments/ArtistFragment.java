package com.arutech.sargam.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.adapter.ArtistSection;
import com.arutech.sargam.adapter.HeterogeneousAdapter;
import com.arutech.sargam.adapter.HeterogeneousFastScrollAdapter;
import com.arutech.sargam.adapter.LibraryEmptyState;
import com.arutech.sargam.data.store.MusicStore;
import com.arutech.sargam.model.Artist;
import com.arutech.sargam.view.BackgroundDecoration;
import com.arutech.sargam.view.DividerDecoration;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class ArtistFragment extends BaseFragment {

    @Inject
    MusicStore mMusicStore;

    private FastScrollRecyclerView mRecyclerView;
    private HeterogeneousAdapter mAdapter;
    private ArtistSection mArtistSection;
    private List<Artist> mArtists;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SargamApplication.getComponent(this).inject(this);
        mMusicStore.getArtists()
                .compose(bindToLifecycle())
                .subscribe(
                        artists -> {
                            mArtists = artists;
                            setupAdapter();
                        }, throwable -> {
                            Timber.e(throwable, "Failed to get all artists from MusicStore");
                        });
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
        mArtistSection = null;
    }

    private void setupAdapter() {
        if (mRecyclerView == null || mArtists == null) {
            return;
        }

        if (mArtistSection != null) {
            mArtistSection.setData(mArtists);
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter = new HeterogeneousFastScrollAdapter();
            mAdapter.setHasStableIds(true);
            mRecyclerView.setAdapter(mAdapter);

            mArtistSection = new ArtistSection(this, mArtists);
            mAdapter.addSection(mArtistSection);
            mAdapter.setEmptyState(new LibraryEmptyState(getActivity()));
        }
    }
}
