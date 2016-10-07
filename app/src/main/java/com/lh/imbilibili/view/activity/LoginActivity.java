package com.lh.imbilibili.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/7.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;
    @BindView(R.id.iv_22)
    ImageView mIv22;
    @BindView(R.id.iv_33)
    ImageView mIv33;
    @BindView(R.id.username)
    EditText mEdUserName;
    @BindView(R.id.password)
    EditText mEdPassword;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        StatusBarUtils.setSimpleToolbarLayout(this, mToolbar);
        initView();
    }

    private void initView() {
        mToolbar.setTitle("登陆");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEdPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mIv22.setImageResource(R.drawable.ic_22_hide);
                    mIv33.setImageResource(R.drawable.ic_33_hide);
                } else {
                    mIv22.setImageResource(R.drawable.ic_22);
                    mIv33.setImageResource(R.drawable.ic_33);
                }
            }
        });
        mScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom != oldBottom) {
                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.scrollTo(0, mScrollView.getChildAt(0).getMeasuredHeight());
                        }
                    });
                }
            }
        });

    }


}
