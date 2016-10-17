package com.lh.imbilibili.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.CommunityRecyclerViewAdapter;
import com.lh.imbilibili.widget.EmptyView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/17.
 */

public class UserCenterCommunityFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;

    private boolean mHaveReceiverEvent;
    private CommunityRecyclerViewAdapter mAdapter;

    public static UserCenterCommunityFragment newInstance() {
        return new UserCenterCommunityFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mHaveReceiverEvent = false;
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new CommunityRecyclerViewAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        BusUtils.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusUtils.getBus().unregister(this);
    }

    @Subscribe
    public void onUserCenterDataLoadFinish(UserCenter userCenter) {
        if (mHaveReceiverEvent) {
            return;
        }
        mHaveReceiverEvent = true;
        if (userCenter.getSetting().getGroups() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_permission);
            mEmptyView.setText(R.string.space_tips_no_permission);
            return;
        }
        if (userCenter.getCommunity().getCount() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_data);
            mEmptyView.setText(R.string.no_data_tips);
        } else {
            mRecyclerView.setEnableLoadMore(true);
            mRecyclerView.setShowLoadingView(true);
            mEmptyView.setVisibility(View.GONE);
            mAdapter.addCommunities(userCenter.getCommunity().getItem());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_list;
    }
}
