package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.Bangumi;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.IndexPage;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.BangumiRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.BangumiRecyclerViewItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/7/6.
 */
public class BangumiFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreRecyclerView.onLoadMoreLinstener {

    private View view;
    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    private IndexPage indexPage;
    private List<Bangumi> bangumis;

    private BaseActivity baseActivity;
    private BangumiRecyclerViewAdapter adapter;

    public static BangumiFragment newInstance() {
        BangumiFragment fragment = new BangumiFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_bangumi, container, false);
            ButterKnife.bind(this, view);
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new BangumiRecyclerViewAdapter(indexPage, bangumis, getActivity(), recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = adapter.getItemViewType(position);
                if (type == BangumiRecyclerViewAdapter.BANNER ||
                        type == BangumiRecyclerViewAdapter.NAV ||
                        type == BangumiRecyclerViewAdapter.LATEST_UPDATE_HEAD ||
                        type == BangumiRecyclerViewAdapter.END_BANGUMI_HEAD ||
                        type == BangumiRecyclerViewAdapter.END_BANGUMI_GROUP ||
                        type == BangumiRecyclerViewAdapter.BANGUMI_RECOMMEND_HEAD ||
                        type == BangumiRecyclerViewAdapter.BANGUMI_RECOMMEND_ITEM ||
                        type == BangumiRecyclerViewAdapter.LOAD_MORE) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.addItemDecoration(new BangumiRecyclerViewItemDecoration(getActivity()));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadMoreLinstener(this);
    }

    private void initData() {
        if (bangumis == null) {
            bangumis = new ArrayList<>();
            adapter.setBangumis(bangumis);
        }
        baseActivity = (BaseActivity) getActivity();
        if (indexPage == null) {
            refreshIndexData();
        }
        if (bangumis == null || bangumis.size() == 0) {
            getBangumiRecommend("-1", 10);
        }
    }

    private void refreshIndexData() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        Call<BiliBiliResultResponse<IndexPage>> indexCall = baseActivity.getApi()
                .getIndexPage(Constant.APPKEY, Constant.BUILD, Constant.MOBI_APP,
                        Constant.MOBI_APP, System.currentTimeMillis() + "");
        indexCall.enqueue(new Callback<BiliBiliResultResponse<IndexPage>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<IndexPage>> call, Response<BiliBiliResultResponse<IndexPage>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body().getCode() == 0) {
                    indexPage = response.body().getResult();
                    adapter.setIndexPage(indexPage);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResultResponse<IndexPage>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getBangumiRecommend(String cursor, int pageSize) {
        Call<BiliBiliResultResponse<List<Bangumi>>> recommendCall = baseActivity.getApi().getBangumiRecommend(Constant.APPKEY, Constant.BUILD, cursor, Constant.MOBI_APP,
                pageSize + "", Constant.MOBI_APP, System.currentTimeMillis() + "");
        recommendCall.enqueue(new Callback<BiliBiliResultResponse<List<Bangumi>>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<List<Bangumi>>> call, Response<BiliBiliResultResponse<List<Bangumi>>> response) {
                if (response.isSuccessful() && response.body().getCode() == 0) {
                    if(response.body().getResult().size() == 0){
                        adapter.setLoadView("再怎么找也没有了",false);
                        recyclerView.setCanLoadMore(false);
                    }
                    bangumis.addAll(response.body().getResult());
                    recyclerView.setLoadingState(false);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResultResponse<List<Bangumi>>> call, Throwable t) {

            }
        });
    }

    @Override
    public String getTitle() {
        return "番剧";
    }

    @Override
    public void onRefresh() {
        refreshIndexData();
        bangumis.clear();
        recyclerView.setCanLoadMore(true);
        getBangumiRecommend("-1", 10);
    }

    @Override
    public void onLoadMore() {
        if (bangumis == null || bangumis.size() == 0) {
            getBangumiRecommend("-1", 10);
        } else {
            getBangumiRecommend(bangumis.get(bangumis.size() - 1).getCursor(), 10);
        }
    }
}
