package com.headmostlab.quickbarbell.utils;

import com.google.android.material.appbar.AppBarLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class AppBarLayoutUtils {

    public static void enableAppBarDragging(AppBarLayout appBarLayout, final boolean newValue) {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return newValue;
            }
        });
        params.setBehavior(behavior);
    }

}
