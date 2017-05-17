package com.arutech.sargam.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.arutech.sargam.databinding.InstanceArtistBinding;
import com.arutech.sargam.model.Artist;
import com.arutech.sargam.model.ModelUtil;
import com.arutech.sargam.viewmodel.ArtistViewModel;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

public class ArtistSection extends HeterogeneousAdapter.ListSection<Artist>
        implements FastScrollRecyclerView.SectionedAdapter {

    private FragmentManager mFragmentManager;

    public ArtistSection(AppCompatActivity activity, @NonNull List<Artist> data) {
        this(activity.getSupportFragmentManager(), data);
    }

    public ArtistSection(Fragment fragment, @NonNull List<Artist> data) {
        this(fragment.getFragmentManager(), data);
    }

    public ArtistSection(FragmentManager fragmentManager, @NonNull List<Artist> data) {
        super(data);
        mFragmentManager = fragmentManager;
    }

    @Override
    public int getId(int position) {
        return get(position).getArtistId();
    }

    @Override
    public EnhancedViewHolder<Artist> createViewHolder(HeterogeneousAdapter adapter,
                                                       ViewGroup parent) {
        InstanceArtistBinding binding = InstanceArtistBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        char firstChar = ModelUtil.sortableTitle(get(position).getArtistName()).charAt(0);
        return Character.toString(firstChar).toUpperCase();
    }

    public class ViewHolder extends EnhancedViewHolder<Artist> {

        private InstanceArtistBinding mBinding;

        public ViewHolder(InstanceArtistBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewModel(new ArtistViewModel(itemView.getContext(), mFragmentManager));
        }

        @Override
        public void onUpdate(Artist item, int sectionPosition) {
            mBinding.getViewModel().setArtist(item);
            mBinding.executePendingBindings();
        }
    }
}
