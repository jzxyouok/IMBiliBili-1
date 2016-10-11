package com.lh.imbilibili.view.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/10.
 */

public class AttentionFragment extends BaseFragment {
    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_attention;
    }
}
