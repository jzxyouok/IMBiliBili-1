package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.search.SearchResult;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.activity.BangumiDetailActivity;
import com.lh.imbilibili.view.activity.VideoDetailActivity;
import com.lh.imbilibili.view.adapter.search.SearchAdapter;
import com.lh.imbilibili.view.adapter.search.SearchItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/5.
 */

public class SearchResultFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, SearchAdapter.OnSearchItemClickListener {
    private static final String EXTRA_DATA = "searchData";
    private static final String EXTRA_KEY = "keyWord";

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private int mCurrentPage;
    private Call<BilibiliDataResponse<SearchResult>> mSearchCall;
    private SearchResult mSearchResult;
    private String mKeyWord;

    public static SearchResultFragment newInstance(String keyWord, SearchResult searchResult) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_KEY, keyWord);
        bundle.putParcelable(EXTRA_DATA, searchResult);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mKeyWord = getArguments().getString(EXTRA_KEY);
        SearchResult searchResult = getArguments().getParcelable(EXTRA_DATA);
        mAdapter = new SearchAdapter(getContext(), searchResult);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        SearchItemDecoration itemDecoration = new SearchItemDecoration(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnSearchItemClickListener(this);
    }

    @Override
    protected void fetchData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_search_result_list;
    }

    private void loadSearchPage(int page) {
        mSearchCall = IMBilibiliApplication.getApplication().getApi().getSearchResult(0, mKeyWord, page, 20, Constant.APPKEY, Constant.BUILD, Constant.MOBI_APP, Constant.PLATFORM);
        mSearchCall.enqueue(new Callback<BilibiliDataResponse<SearchResult>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<SearchResult>> call, Response<BilibiliDataResponse<SearchResult>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().getCode() == 0) {
                    if (response.body().getData().getItems().getArchive().size() != 0) {
                        mSearchResult = response.body().getData();
                        mAdapter.addData(mSearchResult.getItems().getArchive());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView("没有更多了", false);
                    }
                } else {
                    mCurrentPage--;
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<SearchResult>> call, Throwable t) {
                mCurrentPage--;
                mRecyclerView.setLoading(false);
                ToastUtils.showToast(getContext(), "加载失败", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onLoadMore() {
        mCurrentPage++;
        loadSearchPage(mCurrentPage);
    }

    @Override
    public void onSearchItemClick(String param, int type) {
        switch (type) {
            case SearchAdapter.TYPE_SEASON:
                BangumiDetailActivity.startActivity(getContext(), param);
                break;
            case SearchAdapter.TYPE_VIDEO:
                VideoDetailActivity.startActivity(getContext(), param);
                break;
            case SearchAdapter.TYPE_SEASON_MORE:
                if (getActivity() instanceof OnSeasonMoreClickListener) {
                    ((OnSeasonMoreClickListener) getActivity()).onSeasonMoreClick();
                }
                break;
        }
    }

    public interface OnSeasonMoreClickListener {
        void onSeasonMoreClick();
    }
}