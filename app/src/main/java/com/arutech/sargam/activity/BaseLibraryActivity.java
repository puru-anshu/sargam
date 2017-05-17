package com.arutech.sargam.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import com.arutech.sargam.R;
import com.arutech.sargam.databinding.ActivityLibraryBaseWrapperBinding;
import com.arutech.sargam.viewmodel.BaseLibraryActivityViewModel;


public abstract class BaseLibraryActivity extends BaseActivity {

    private static final String KEY_WAS_NOW_PLAYING_EXPANDED = "NowPlayingPageExpanded";

    private ActivityLibraryBaseWrapperBinding mBinding;
    private BaseLibraryActivityViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_library_base_wrapper);
        mViewModel = new BaseLibraryActivityViewModel(this, !isToolbarCollapsing());
        mBinding.setViewModel(mViewModel);

        getLayoutInflater().inflate(getContentLayoutResource(),
                mBinding.libraryBaseWrapperContainer, true);

        setupToolbar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (savedInstanceState != null
                && savedInstanceState.getBoolean(KEY_WAS_NOW_PLAYING_EXPANDED, false)) {
            expandBottomSheet();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        BottomSheetBehavior<View> bottomSheet = BottomSheetBehavior.from(mBinding.miniplayerHolder);
        boolean expanded = bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED;
        outState.putBoolean(KEY_WAS_NOW_PLAYING_EXPANDED, expanded);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewModel.onActivityExitForeground();
        mBinding.executePendingBindings();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.onActivityEnterForeground();
        mBinding.executePendingBindings();
    }

    @Override
    public void onBackPressed() {
        BottomSheetBehavior<View> bottomSheet = BottomSheetBehavior.from(mBinding.miniplayerHolder);
        if (bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void showSnackbar(String message) {
        if (mBinding.libraryBaseWrapperContainer.getVisibility() == View.VISIBLE) {
            super.showSnackbar(message);
        }
    }

    @LayoutRes
    protected abstract int getContentLayoutResource();

    public boolean isToolbarCollapsing() {
        return false;
    }

    public void expandBottomSheet() {
        BottomSheetBehavior<View> bottomSheet = BottomSheetBehavior.from(mBinding.miniplayerHolder);
        bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

}
