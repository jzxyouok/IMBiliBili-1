package com.lh.imbilibili.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.GameRecyclerViewAdapter;
import com.lh.imbilibili.widget.EmptyView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/17.
 * 用户中心游戏页面
 */

public class UserCenterGameFragment extends BaseFragment {
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;

    private boolean mHaveReceiverEvent;
    private GameRecyclerViewAdapter mAdapter;

    public static UserCenterGameFragment newInstance() {
        return new UserCenterGameFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mHaveReceiverEvent = false;
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new GameRecyclerViewAdapter(getContext());
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
        if (userCenter.getSetting().getPlayedGame() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_permission);
            mEmptyView.setText(R.string.space_tips_no_permission);
            return;
        }
        if (userCenter.getGame().getCount() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_data);
            mEmptyView.setText(R.string.no_data_tips);
        } else {
            mRecyclerView.setEnableLoadMore(true);
            mRecyclerView.setShowLoadingView(true);
            mEmptyView.setVisibility(View.GONE);
            mAdapter.addGames(userCenter.getGame().getItem());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_list;
    }
}
