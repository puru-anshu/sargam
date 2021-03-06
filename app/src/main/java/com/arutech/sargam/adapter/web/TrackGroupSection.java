package com.arutech.sargam.adapter.web;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.arutech.sargam.adapter.EnhancedViewHolder;
import com.arutech.sargam.adapter.HeterogeneousAdapter;
import com.arutech.sargam.databinding.InstanceTrackGroupBinding;
import com.arutech.sargam.model.ModelUtil;
import com.arutech.sargam.saavn.api.model.TrackGroup;
import com.arutech.sargam.viewmodel.web.TrackGroupViewModel;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

public class TrackGroupSection extends HeterogeneousAdapter.ListSection<TrackGroup>
		implements FastScrollRecyclerView.SectionedAdapter {

	private FragmentManager mFragmentManager;

	public TrackGroupSection(AppCompatActivity activity, @NonNull List<TrackGroup> data) {
		this(activity.getSupportFragmentManager(), data);
	}

	public TrackGroupSection(Fragment fragment, @NonNull List<TrackGroup> data) {
		this(fragment.getFragmentManager(), data);
	}

	public TrackGroupSection(FragmentManager fragmentManager, @NonNull List<TrackGroup> data) {
		super(data);
		mFragmentManager = fragmentManager;
	}

	@Override
	public int getId(int position) {
		return (int) get(position).getId().hashCode();
	}

	@Override
	public EnhancedViewHolder<TrackGroup> createViewHolder(HeterogeneousAdapter adapter,
	                                                       ViewGroup parent) {
		InstanceTrackGroupBinding binding = InstanceTrackGroupBinding.inflate(
				LayoutInflater.from(parent.getContext()), parent, false);

		return new ViewHolder(binding);
	}

	@NonNull
	@Override
	public String getSectionName(int position) {
		char firstChar = ModelUtil.sortableTitle(get(position).getTitle()).charAt(0);
		return Character.toString(firstChar).toUpperCase();
	}

	private class ViewHolder extends EnhancedViewHolder<TrackGroup> {

		private InstanceTrackGroupBinding mBinding;

		public ViewHolder(InstanceTrackGroupBinding binding) {
			super(binding.getRoot());
			mBinding = binding;
			mBinding.setViewModel(new TrackGroupViewModel(itemView.getContext(), mFragmentManager));
		}

		@Override
		public void onUpdate(TrackGroup item, int sectionPosition) {
			mBinding.getViewModel().setTrackGroup(item);
			mBinding.executePendingBindings();
		}
	}
}
