package com.arutech.sargam.view;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;

public class DragDividerDecoration extends DividerDecoration {

    /**
     * Create an ItemDecorator for use with a RecyclerView
     *
     * @param context           A context held temporarily to get colors and display metrics
     * @param excludedLayoutIDs A list of layoutIDs to exclude adding a divider to
     */
    public DragDividerDecoration(Context context, @IdRes int... excludedLayoutIDs) {
        super(context, excludedLayoutIDs);
    }

    /**
     * Create an ItemDecorator for use with a RecyclerView
     * @param context A context held temporarily to get colors and display metrics
     * @param drawOnLastItem Whether or not to draw a divider under the last item in the list
     * @param excludedLayoutIDs A list of layoutIDs to exclude adding a divider to
     *                          none to add a divider to each entry in the RecyclerView
     */
    public DragDividerDecoration(Context context, boolean drawOnLastItem,
                                 @IdRes int... excludedLayoutIDs) {
        super(context, drawOnLastItem, excludedLayoutIDs);
    }

    @Override
    protected boolean includeView(View view) {
        return view.getTag() == null && super.includeView(view);
    }
}
