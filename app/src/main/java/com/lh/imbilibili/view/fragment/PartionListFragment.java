package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.PartionHome;
import com.lh.imbilibili.model.PartionVideo;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.activity.VideoDetailActivity;
import com.lh.imbilibili.view.adapter.categoryfragment.PartionChildRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.categoryfragment.PartionListItemDecoration;
import com.lh.imbilibili.view.adapter.categoryfragment.model.PartionModel;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/1.
 */

public class PartionListFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, PartionChildRecyclerViewAdapter.OnVideoItemClickListener {

    private static final String EXTRA_DATA = "partion";

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private PartionModel.Partion mPartion;

    private PartionChildRecyclerViewAdapter mAdapter;

    private int mCurrentPage;

    private Call<BilibiliDataResponse<PartionHome>> mPartionDataCall;
    private Call<BilibiliDataResponse<List<PartionVideo>>> mNewVideoDataCall;

    public static PartionListFragment newInstance(PartionModel.Partion partion) {
        PartionListFragment fragment = new PartionListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DATA, partion);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_partion_list;
    }

    @Override
    protected void initView(View view) {
        mPartion = getArguments().getParcelable(EXTRA_DATA);
        ButterKnife.bind(this, view);
        mCurrentPage = 1;
        initRecyclerView();
    }

    @Override
    protected void fetchData() {
        loadData();
        loadNewData(mCurrentPage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new PartionChildRecyclerViewAdapter(getContext());
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        PartionListItemDecoration itemDecoration = new PartionListItemDecoration(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnVideoItemClickListener(this);
    }

    private void loadNewData(int page) {
        mNewVideoDataCall = IMBilibiliApplication.getApplication().getApi().getPartionChildList(mPartion.getId(), page, 20, "senddate", Constant.APPKEY, Constant.BUILD, Constant.MOBI_APP);
        mNewVideoDataCall.enqueue(new Callback<BilibiliDataResponse<List<PartionVideo>>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<List<PartionVideo>>> call, Response<BilibiliDataResponse<List<PartionVideo>>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().getCode() == 0) {
                    if (response.body().getData().size() == 0) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView("没有更多了", false);
                        mCurrentPage--;
                    } else {
                        mAdapter.addNewVideos(response.body().getData());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<List<PartionVideo>>> call, Throwable t) {
                mCurrentPage--;
                mRecyclerView.setLoading(false);
                ToastUtils.showToast(getContext(), "加载失败", Toast.LENGTH_SHORT);
            }
        });
    }

    private void loadData() {
        mPartionDataCall = IMBilibiliApplication.getApplication().getApi().getPartionChild(mPartion.getId(), "*", Constant.APPKEY, Constant.BUILD, Constant.MOBI_APP);
        mPartionDataCall.enqueue(new Callback<BilibiliDataResponse<PartionHome>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<PartionHome>> call, Response<BilibiliDataResponse<PartionHome>> response) {
                if (response.body().getCode() == 0) {
                    mAdapter.setPartionHomeData(response.body().getData());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<PartionHome>> call, Throwable t) {
                ToastUtils.showToast(getContext(), "加载失败", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onLoadMore() {
        mCurrentPage++;
        loadNewData(mCurrentPage);
    }

    @Override
    public void onVideoClick(String aid) {
        VideoDetailActivity.startActivity(getContext(), aid);
    }
}
