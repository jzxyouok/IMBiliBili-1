package com.lh.imbilibili.view.adapter.usercenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lh.imbilibili.view.BaseFragment;

/**
 * Created by liuhui on 2016/10/16.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private BaseFragment[] mFragments;

    public ViewPagerAdapter(FragmentManager fm, BaseFragment[] fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }
}
