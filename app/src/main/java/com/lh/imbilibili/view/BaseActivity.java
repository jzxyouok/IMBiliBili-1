package com.lh.imbilibili.view;

import android.graphics.Color;
import android.os.Build;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
            int statusBarHeight = getResources().getDimensionPixelSize(R.dimen.status_bar_height);
            View view = new View(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            viewGroup.addView(view, params);
            View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
        } else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
//            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
        }
    }
}
