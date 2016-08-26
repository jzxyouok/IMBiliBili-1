package com.lh.imbilibili.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/7/5.
 */
public class BaseActivity extends AppCompatActivity {

    protected void initStatusBar() {
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        int statusBarHeight = getResources().getDimensionPixelSize(R.dimen.status_bar_height);
        View view = new View(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        viewGroup.addView(view, params);
    }
}
