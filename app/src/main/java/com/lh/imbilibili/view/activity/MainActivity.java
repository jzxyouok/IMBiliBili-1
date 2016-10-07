package com.lh.imbilibili.view.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.fragment.MainFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements IDrawerLayoutActivity, View.OnClickListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer)
    NavigationView mDrawer;

    private ImageView mIvAvatar;
    private TextView mTvNickName;
    private ImageView mIvLoginBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mIvAvatar = (ImageView) mDrawer.getHeaderView(0).findViewById(R.id.avatar);
        mTvNickName = (TextView) mDrawer.getHeaderView(0).findViewById(R.id.nick_name);
        mIvLoginBg = (ImageView) mDrawer.getHeaderView(0).findViewById(R.id.login_bg);
        initView();
        initFragmentView();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDrawer.getHeaderView(0).setPadding(0, StatusBarUtils.getStatusBarHeight(this), 0, 0);
        }
        mIvAvatar.setOnClickListener(this);
        mTvNickName.setOnClickListener(this);
    }

    private void initFragmentView() {
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.feedback_container, mainFragment, MainFragment.TAG).commit();
    }

    @Override
    public void openDrawer() {
        mDrawerLayout.openDrawer(mDrawer);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mDrawer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
            case R.id.nick_name:
                LoginActivity.startActivity(this);
                break;
        }
    }
}
