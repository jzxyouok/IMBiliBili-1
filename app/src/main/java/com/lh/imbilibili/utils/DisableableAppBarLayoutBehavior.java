package com.lh.imbilibili.utils;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by liuhui on 2016/10/3.
 */

public class DisableableAppBarLayoutBehavior extends AppBarLayout.Behavior {

    private boolean isEnableScroll = true;

    public DisableableAppBarLayoutBehavior() {
    }

    public DisableableAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEnableScroll(boolean enableScroll) {
        isEnableScroll = enableScroll;
    }

    public boolean isEnableScroll() {
        return isEnableScroll;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return isEnableScroll && super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        return isEnableScroll && super.onInterceptTouchEvent(parent, child, ev);
    }
}
