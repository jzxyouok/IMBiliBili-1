package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.MainViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/7/6.
 */
public class MainFragment extends BaseFragment {

    public static final String TAG = "MainFragment";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.account_badge)
    ImageView ivAccountBadge;
    @BindView(R.id.notice_badge)
    ImageView ivNoticeBadge;
    @BindView(R.id.nick_name)
    TextView tvNickName;
    private List<BaseFragment> fragments;
    private MainViewPagerAdapter adapter;

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
        StatusBarUtils.setDrawerToolbarTabLayout(getActivity(), coordinatorLayout, (ViewGroup) getActivity().findViewById(R.id.drawer));
        fragments = new ArrayList<>();
        fragments.add(BangumiFragment.newInstance());
        fragments.add(CategoryFragment.newInstance());
        adapter = new MainViewPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public String getTitle() {
        return null;
    }
}
