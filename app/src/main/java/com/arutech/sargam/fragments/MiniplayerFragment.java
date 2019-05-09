package com.arutech.sargam.fragments;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.databinding.FragmentMiniplayerBinding;
import com.arutech.sargam.viewmodel.MiniplayerViewModel;

public class MiniplayerFragment extends BaseFragment {

    private FragmentMiniplayerBinding mBinding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SargamApplication.getComponent(getContext()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentMiniplayerBinding.inflate(inflater, container, false);
        mBinding.setViewModel(new MiniplayerViewModel(this));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ProgressBar progressBar = mBinding.miniplayerProgress;
            LayerDrawable progressBarDrawable = (LayerDrawable) progressBar.getProgressDrawable();

            Drawable progress = progressBarDrawable.findDrawableByLayerId(android.R.id.progress);

        }

        return mBinding.getRoot();
    }

}
