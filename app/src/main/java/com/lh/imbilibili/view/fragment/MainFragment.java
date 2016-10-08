package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.activity.IDrawerLayoutActivity;
import com.lh.imbilibili.view.activity.SearchActivity;
import com.lh.imbilibili.view.activity.VideoDetailActivity;
import com.lh.imbilibili.view.adapter.MainViewPagerAdapter;
import com.lh.imbilibili.widget.BiliBiliSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/7/6.
 */
public class MainFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener, BiliBiliSearchView.OnSearchListener {

    public static final String TAG = "MainFragment";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.avatar)
    ImageView mIvAvatar;
    @BindView(R.id.account_badge)
    ImageView mIvAccountBadge;
    @BindView(R.id.notice_badge)
    ImageView mIvNoticeBadge;
    @BindView(R.id.nick_name)
    TextView mTvNickName;
    @BindView(R.id.navigation)
    View mDrawHome;

    private List<BaseFragment> fragments;
    private MainViewPagerAdapter adapter;
    private BiliBiliSearchView mSearchView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_main_home;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbar.inflateMenu(R.menu.main_menu);
        mToolbar.setOnMenuItemClickListener(this);
        StatusBarUtils.setDrawerToolbarTabLayout(getActivity(), mCoordinatorLayout);
        fragments = new ArrayList<>();
        fragments.add(BangumiFragment.newInstance());
        fragments.add(CategoryFragment.newInstance());
        mTabs.setTabTextColors(ContextCompat.getColor(getContext(), R.color.gray_light), ContextCompat.getColor(getContext(), R.color.white));
        adapter = new MainViewPagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mViewPager);
        initToolbar();
    }

    private void initToolbar() {
        mIvAvatar.setImageResource(R.drawable.bili_default_avatar);
        mTvNickName.setText("未登录");
        mIvAccountBadge.setVisibility(View.GONE);
        mSearchView = BiliBiliSearchView.newInstance();
        mSearchView.setHint("搜索视频、番剧、up主或av号");
        mSearchView.setOnSearchListener(this);
        mDrawHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof IDrawerLayoutActivity) {
                    ((IDrawerLayoutActivity) getActivity()).openDrawer();
                }
            }
        });
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_search) {
            mSearchView.show(getChildFragmentManager(), "search");
        }
        return true;
    }

    @Override
    public void onSearch(String keyWord) {
        if (keyWord.matches("^av\\d+$")) {
            VideoDetailActivity.startActivity(getContext(), keyWord.replaceAll("av", ""));
        } else {
            SearchActivity.startActivity(getContext(), keyWord);
        }
    }
}
