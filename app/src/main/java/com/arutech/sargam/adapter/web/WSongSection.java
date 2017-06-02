package com.arutech.sargam.adapter.web;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.arutech.sargam.activity.BaseActivity;
import com.arutech.sargam.adapter.EnhancedViewHolder;
import com.arutech.sargam.adapter.HeterogeneousAdapter;
import com.arutech.sargam.databinding.InstanceWebSongBinding;
import com.arutech.sargam.fragments.BaseFragment;
import com.arutech.sargam.model.ModelUtil;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.viewmodel.web.WSongViewModel;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

public class WSongSection extends HeterogeneousAdapter.ListSection<Song>
		implements FastScrollRecyclerView.SectionedAdapter {

	private BaseActivity mActivity;
	private BaseFragment mFragment;

	public WSongSection(BaseActivity activity, @NonNull List<Song> data) {
		super(data);
		mActivity = activity;
	}

	public WSongSection(BaseFragment fragment, @NonNull List<Song> data) {
		super(data);
		mFragment = fragment;
	}

	@Override
	public int getId(int position) {
		return (int) get(position).getSongId();
	}

	@Override
	public EnhancedViewHolder<Song> createViewHolder(HeterogeneousAdapter adapter,
	                                                 ViewGroup parent) {


		InstanceWebSongBinding binding = InstanceWebSongBinding.inflate(
				LayoutInflater.from(parent.getContext()), parent, false);

		return new ViewHolder(binding, getData());
	}

	@NonNull
	@Override
	public String getSectionName(int position) {
		char firstChar = ModelUtil.sortableTitle(get(position).getSongName()).charAt(0);
		return Character.toString(firstChar).toUpperCase();
	}

	private class ViewHolder extends EnhancedViewHolder<Song> {

		private InstanceWebSongBinding mBinding;

		public ViewHolder(InstanceWebSongBinding binding, List<Song> songList) {
			super(binding.getRoot());
			mBinding = binding;

			if (mFragment != null) {
				binding.setViewModel(new WSongViewModel(mFragment, songList));
			} else if (mActivity != null) {
				binding.setViewModel(new WSongViewModel(mActivity, songList));
			} else {
				throw new RuntimeException("Unable to create view model. This SongSection has not "
						+ "been created with a valid activity or fragment");
			}
		}

		@Override
		public void onUpdate(Song s, int sectionPosition) {
			mBinding.getViewModel().setSong(getData(), sectionPosition);
			mBinding.executePendingBindings();
		}
	}
}
