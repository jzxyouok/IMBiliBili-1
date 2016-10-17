package com.lh.imbilibili.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/10/17.
 */

public class EmptyView extends LinearLayout {

    private ImageView mIv;
    private TextView mTv;

    public EmptyView(Context context) {
        super(context);
        init(context);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        mIv = new ImageView(context);
        mTv = new TextView(context);
        LayoutParams ivParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams tvParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvParams.gravity = Gravity.CENTER_HORIZONTAL;
        tvParams.topMargin = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        mTv.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
        addView(mIv, ivParams);
        addView(mTv, tvParams);
    }

    public void setImgResource(@DrawableRes int resId) {
        mIv.setImageResource(resId);
    }

    public void setText(String msg) {
        mTv.setText(msg);
    }

    public void setText(@StringRes int resId) {
        mTv.setText(resId);
    }
}
