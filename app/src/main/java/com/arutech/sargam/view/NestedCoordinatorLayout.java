package com.arutech.sargam.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.WindowInsets;

public class NestedCoordinatorLayout extends CoordinatorLayout {

    public NestedCoordinatorLayout(Context context) {
        super(context);
    }

    public NestedCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        super.dispatchApplyWindowInsets(insets);

        for (int i = 0; i < getChildCount(); i++) {
            // Do NOT allow children to consume these insets
            getChildAt(i).dispatchApplyWindowInsets(insets);
        }

        return insets;
    }
}
