package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.adapter.usercenter.HomeRecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/16.
 */

public class UserCenterHomeFragment extends LazyLoadFragment {

    private static final String EXTRA_DATA = "Data";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private UserCenter mUserCenter;

    public static UserCenterHomeFragment newInstance(UserCenter userCenter) {
        UserCenterHomeFragment fragment = new UserCenterHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DATA, userCenter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void fetchData() {
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mUserCenter = getArguments().getParcelable(EXTRA_DATA);
        initRecyclerView();
    }

    private void initRecyclerView() {
        final HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(getContext(), mUserCenter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = adapter.getItemViewType(position);
                switch (type) {
                    case HomeRecyclerViewAdapter.TYPE_ARCHIVE_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_ARCHIVE_ITEM:
                    case HomeRecyclerViewAdapter.TYPE_COIN_ARCHIVE_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_COIN_ARCHIVE_ITEM:
                    case HomeRecyclerViewAdapter.TYPE_FAVOURITE_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_FOLLOW_BANGUMI_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_COMMUNITY_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_COMMUNITY_ITEM:
                    case HomeRecyclerViewAdapter.TYPE_GAME_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_GAME_ITEM:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_home;
    }
}
