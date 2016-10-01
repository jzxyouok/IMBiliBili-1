package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.PartionHome;
import com.lh.imbilibili.model.PartionVideo;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.categoryfragment.PartionHomeRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.categoryfragment.PartionItemDecoration;
import com.lh.imbilibili.view.adapter.categoryfragment.model.PartionModel;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/9/29.
 */

public class PartionHomeFragment extends BaseFragment implements LoadMoreRecyclerView.onLoadMoreLinstener, SwipeRefreshLayout.OnRefreshListener {
    private static final String EXTRA_DATA = "partionModel";

    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private PartionHome mPartionHomeData;
    private PartionHomeRecyclerViewAdapter mAdapter;
    private PartionModel mPartionModel;
    private Call<BilibiliDataResponse<PartionHome>> mPartionHomeDataCall;
    private Call<BilibiliDataResponse<List<PartionVideo>>> mPartionDynamicCall;

    private int mCurrentPage = 1;

    public static PartionHomeFragment newInstance(PartionModel partionModel) {
        PartionHomeFragment fragment = new PartionHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DATA, partionModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_partion_home;
    }

    private void loadData() {
        mPartionHomeDataCall = IMBilibiliApplication.getApplication().getApi().getPartionInfo(mPartionModel.getId(), "*", Constant.APPKEY, Constant.BUILD, Constant.MOBI_APP);
        mPartionHomeDataCall.enqueue(new Callback<BilibiliDataResponse<PartionHome>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<PartionHome>> call, Response<BilibiliDataResponse<PartionHome>> response) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (response.body().getCode() == 0) {
                    mPartionHomeData = response.body().getData();
                    mAdapter.setPartionData(mPartionHomeData);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<PartionHome>> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadDynamicData(int page) {
        mPartionDynamicCall = IMBilibiliApplication.getApplication().getApi().getPartionDynamic(mPartionModel.getId(), page, 50, Constant.APPKEY, Constant.BUILD, Constant.MOBI_APP);
        mPartionDynamicCall.enqueue(new Callback<BilibiliDataResponse<List<PartionVideo>>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<List<PartionVideo>>> call, Response<BilibiliDataResponse<List<PartionVideo>>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().getCode() == 0) {
                    if (response.body().getData().size() == 0) {
                        mCurrentPage--;
                        mRecyclerView.setLoadView("没有更多了", false);
                        mRecyclerView.setEnableLoadMore(false);
                    }
                    mAdapter.addDynamicVideo(response.body().getData());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<List<PartionVideo>>> call, Throwable t) {
                mCurrentPage--;
                mRecyclerView.setLoading(false);
            }
        });
    }


    @Override
    protected void initView(View view) {
        mPartionModel = getArguments().getParcelable(EXTRA_DATA);
        ButterKnife.bind(this, view);
        initRecyclerView();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        loadData();
        loadDynamicData(1);
    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case PartionHomeRecyclerViewAdapter.LOAD_MORE:
                    case PartionHomeRecyclerViewAdapter.TYPE_BANNER:
                    case PartionHomeRecyclerViewAdapter.TYPE_HOT_RECOMMEND_HEAD:
                    case PartionHomeRecyclerViewAdapter.TYPE_NEW_VIDEO_HEAD:
                    case PartionHomeRecyclerViewAdapter.TYPE_PARTION_DYNAMIC_HEAD:
                    case PartionHomeRecyclerViewAdapter.TYPE_SUB_PARTION:
                        return 2;
                    case PartionHomeRecyclerViewAdapter.TYPE_HOT_RECOMMEND_ITEM:
                    case PartionHomeRecyclerViewAdapter.TYPE_NEW_VIDEO_ITEM:
                    case PartionHomeRecyclerViewAdapter.TYPE_PARTION_DYNAMIC_ITME:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
        PartionItemDecoration itemDecoration = new PartionItemDecoration(getContext());
        mAdapter = new PartionHomeRecyclerViewAdapter(getContext(), mPartionModel);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setOnLoadMoreLinstener(this);
    }

    @Override
    public void onLoadMore() {
        mCurrentPage++;
        loadDynamicData(mCurrentPage);
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        loadData();
        loadDynamicData(mCurrentPage);
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setLoadView("加载中", true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mPartionDynamicCall, mPartionHomeDataCall);
    }
}
