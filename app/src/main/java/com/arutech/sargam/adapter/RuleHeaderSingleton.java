package com.arutech.sargam.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.arutech.sargam.databinding.InstanceRulesHeaderBinding;
import com.arutech.sargam.model.AutoPlaylist;
import com.arutech.sargam.viewmodel.RuleHeaderViewModel;

public class RuleHeaderSingleton extends HeterogeneousAdapter.SingletonSection<AutoPlaylist.Builder> {

    private AutoPlaylist mOriginalPlaylist;

    public RuleHeaderSingleton(AutoPlaylist playlist, AutoPlaylist.Builder editor) {
        super(editor);
        mOriginalPlaylist = playlist;
    }

    @Override
    public EnhancedViewHolder<AutoPlaylist.Builder> createViewHolder(HeterogeneousAdapter adapter,
                                                                     ViewGroup parent) {
        InstanceRulesHeaderBinding binding = InstanceRulesHeaderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    private class ViewHolder extends EnhancedViewHolder<AutoPlaylist.Builder> {

        private InstanceRulesHeaderBinding mBinding;
        private RuleHeaderViewModel mViewModel;

        public ViewHolder(InstanceRulesHeaderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            mViewModel = new RuleHeaderViewModel(itemView.getContext());
            mViewModel.setOriginalReference(mOriginalPlaylist);

            mBinding.setViewModel(mViewModel);
        }

        @Override
        public void onUpdate(AutoPlaylist.Builder item, int position) {
            mViewModel.setBuilder(item);
        }
    }
}
