package com.lh.imbilibili.view.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.search.UpSearchResult;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.LoadAnimationUtils;
import com.lh.imbilibili.utils.RetrofitHelper;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.adapter.search.SearchItemDecoration;
import com.lh.imbilibili.view.adapter.search.UpUserSearchAdapter;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/6.
 */

public class SearchUpFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener {
    private static final String EXTRA_DATA = "keyWord";

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.loading)
    ImageView mIvLoading;

    private String mKeyWord;
    private Call<BilibiliDataResponse<UpSearchResult>> mSearchCall;
    private UpSearchResult mSearchResult;
    private UpUserSearchAdapter mAdapter;
    private int mCurrentPage;
    private AnimationDrawable mLoadingDrawable;

    public static SearchUpFragment newInstance(String keyWord) {
        SearchUpFragment fragment = new SearchUpFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DATA, keyWord);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mKeyWord = getArguments().getString(EXTRA_DATA);
        mLoadingDrawable = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.anim_search_loading);
        initRecyclerView();
        mCurrentPage = 1;
        mIvLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void fetchData() {
        loadSearchPage(mCurrentPage);
    }

    private void initRecyclerView() {
        mAdapter = new UpUserSearchAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        SearchItemDecoration itemDecoration = new SearchItemDecoration(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mRecyclerView.setEnableLoadMore(false);
        mRecyclerView.setShowLoadingView(false);
    }

    private void loadSearchPage(int page) {
        if (mCurrentPage == 1) {
            LoadAnimationUtils.startLoadAnimate(mIvLoading, R.drawable.anim_search_loading);
        }
        mSearchCall = RetrofitHelper.getInstance().getSearchService().getUpSearchResult(mKeyWord, page, 20, 2);
        mSearchCall.enqueue(new Callback<BilibiliDataResponse<UpSearchResult>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<UpSearchResult>> call, Response<BilibiliDataResponse<UpSearchResult>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().isSuccess()) {
                    if (response.body().getData().getItems() != null) {
                        if (response.body().getData().getPages() == mCurrentPage) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLoadView("没有更多了", false);
                        }
                        if (mCurrentPage == 1) {
                            LoadAnimationUtils.stopLoadAnimate(mIvLoading, 0);
                            mRecyclerView.setEnableLoadMore(true);
                            mRecyclerView.setShowLoadingView(true);
                        }
                        mSearchResult = response.body().getData();
                        mAdapter.addData(mSearchResult.getItems());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        if (mCurrentPage == 1) {
                            LoadAnimationUtils.stopLoadAnimate(mIvLoading, R.drawable.search_failed);
                        }
                        mCurrentPage--;
                    }
                } else {
                    mCurrentPage--;
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<UpSearchResult>> call, Throwable t) {
                mCurrentPage--;
                mRecyclerView.setLoading(false);
                ToastUtils.showToast(getContext(), "加载失败", Toast.LENGTH_SHORT);
                LoadAnimationUtils.stopLoadAnimate(mIvLoading, R.drawable.search_failed);
                mRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mSearchCall);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_search_result_list;
    }

    @Override
    public void onLoadMore() {
        mCurrentPage++;
        loadSearchPage(mCurrentPage);
    }
}
