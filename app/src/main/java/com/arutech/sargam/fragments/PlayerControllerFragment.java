package com.arutech.sargam.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arutech.sargam.databinding.ViewNowPlayingControlPanelBinding;
import com.arutech.sargam.viewmodel.NowPlayingControllerViewModel;

public class PlayerControllerFragment extends BaseFragment {

    private ViewNowPlayingControlPanelBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = ViewNowPlayingControlPanelBinding.inflate(inflater, container, false);
        mBinding.setViewModel(new NowPlayingControllerViewModel(this));

        Drawable progress = mBinding.nowPlayingControllerScrubber.nowPlayingSeekBar.getProgressDrawable();
        if (progress instanceof StateListDrawable) {
            progress = progress.getCurrent();
        }
        if (progress instanceof LayerDrawable) {
            ((LayerDrawable) progress)
                    .findDrawableByLayerId(android.R.id.background)
                    .setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
        }

        return mBinding.getRoot();
    }

}
