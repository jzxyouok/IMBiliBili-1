package com.lh.imbilibili.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/7/9.
 */
public class CardFrameLayout extends FrameLayout {

    public CardFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public CardFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CardFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundResource(R.drawable.bili_default_card_background);
    }
}
