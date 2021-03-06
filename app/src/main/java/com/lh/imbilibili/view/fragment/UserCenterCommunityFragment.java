package com.lh.imbilibili.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.CommunityRecyclerViewAdapter;
import com.lh.imbilibili.widget.EmptyView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/17.
 */

public class UserCenterCommunityFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener {

    private static final int PAGE_SIZE = 10;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;

    private UserCenter mUserCenter;

    private CommunityRecyclerViewAdapter mAdapter;

    private Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Community>>> mCommunityCall;

    private int mCurrentPage;

    public static UserCenterCommunityFragment newInstance() {
        return new UserCenterCommunityFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mCurrentPage = 2;
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new CommunityRecyclerViewAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
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
        if (mUserCenter != null) {
            return;
        }
        mUserCenter = userCenter;
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
            mRecyclerView.setShowLoadingView(true);
            if (userCenter.getCommunity().getCount() <= PAGE_SIZE) {
                mRecyclerView.setEnableLoadMore(false);
                mRecyclerView.setLoadView(R.string.no_data_tips, false);
            } else {
                mRecyclerView.setEnableLoadMore(true);
            }
            mEmptyView.setVisibility(View.GONE);
            mAdapter.addCommunities(userCenter.getCommunity().getItem());
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadCommunity() {
        mCommunityCall = RetrofitHelper.getInstance().getUserService().getUserCommunity(mCurrentPage, PAGE_SIZE, System.currentTimeMillis(), Integer.parseInt(mUserCenter.getCard().getMid()));
        mCommunityCall.enqueue(new Callback<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Community>>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Community>>> call, Response<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Community>>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().isSuccess()) {
                    if (response.body().getData().getCount() < PAGE_SIZE) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView(R.string.no_data_tips, false);
                    } else {
                        int startPosition = mAdapter.getItemCount();
                        mAdapter.addCommunities(response.body().getData().getItem());
                        mAdapter.notifyItemRangeInserted(startPosition, response.body().getData().getItem().size());
                        mCurrentPage++;
                    }
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Community>>> call, Throwable t) {
                mRecyclerView.setLoading(false);
                ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mCommunityCall);
    }

    @Override
    public void onLoadMore() {
        loadCommunity();
    }
}
