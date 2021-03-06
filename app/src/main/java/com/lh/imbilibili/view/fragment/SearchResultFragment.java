package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.search.SearchResult;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.activity.BangumiDetailActivity;
import com.lh.imbilibili.view.activity.VideoDetailActivity;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.search.SearchRecyclerViewAdapter;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/5.
 */

public class SearchResultFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, SearchRecyclerViewAdapter.OnSearchItemClickListener {

    private static final int PAGE_SIZE = 20;

    private static final String EXTRA_DATA = "searchData";
    private static final String EXTRA_KEY = "keyWord";

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    private SearchRecyclerViewAdapter mAdapter;
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
        mCurrentPage = 2;
        mAdapter = new SearchRecyclerViewAdapter(getContext(), searchResult);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutItemDecoration itemDecoration = new LinearLayoutItemDecoration(getContext());
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

    private void loadSearchPage() {
        mSearchCall = RetrofitHelper.getInstance().getSearchService().getSearchResult(0, mKeyWord, mCurrentPage, PAGE_SIZE);
        mSearchCall.enqueue(new Callback<BilibiliDataResponse<SearchResult>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<SearchResult>> call, Response<BilibiliDataResponse<SearchResult>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().getCode() == 0) {
                    if (response.body().getData().getItems().getArchive().size() != 0) {
                        mSearchResult = response.body().getData();
                        int startPosition = mAdapter.getItemCount();
                        mAdapter.addData(mSearchResult.getItems().getArchive());
                        mAdapter.notifyItemRangeInserted(startPosition, mSearchResult.getItems().getArchive().size());
                        mCurrentPage++;
                    } else {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView(R.string.no_data_tips, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<SearchResult>> call, Throwable t) {
                mRecyclerView.setLoading(false);
                ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onLoadMore() {
        loadSearchPage();
    }

    @Override
    public void onSearchItemClick(String param, int type) {
        switch (type) {
            case SearchRecyclerViewAdapter.TYPE_SEASON:
                BangumiDetailActivity.startActivity(getContext(), param);
                break;
            case SearchRecyclerViewAdapter.TYPE_VIDEO:
                VideoDetailActivity.startActivity(getContext(), param);
                break;
            case SearchRecyclerViewAdapter.TYPE_SEASON_MORE:
                if (getActivity() instanceof OnSeasonMoreClickListener) {
                    ((OnSeasonMoreClickListener) getActivity()).onSeasonMoreClick();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mSearchCall);
    }

    public interface OnSeasonMoreClickListener {
        void onSeasonMoreClick();
    }
}
