package com.lh.imbilibili.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.Banner;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.IndexBangumiRecommend;
import com.lh.imbilibili.model.IndexPage;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.activity.BangumiDetailActivity;
import com.lh.imbilibili.view.activity.SeasonGroupActivity;
import com.lh.imbilibili.view.activity.WebViewActivity;
import com.lh.imbilibili.view.adapter.bangumifragment.BangumiAdapter;
import com.lh.imbilibili.view.adapter.bangumifragment.BangumiIndexItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/7/6.
 */
public class BangumiFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreRecyclerView.onLoadMoreLinstener, BangumiAdapter.OnItemClickListener {

    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView recyclerView;

    private IndexPage indexData;
    private List<IndexBangumiRecommend> bangumis;

    private BangumiAdapter adapter;

    private Call<BiliBiliResultResponse<IndexPage>> indexCall;
    private Call<BiliBiliResultResponse<List<IndexBangumiRecommend>>> recommendCall;

    public static BangumiFragment newInstance() {
        return new BangumiFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bangumi, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        loadAllData(false);
    }

    private void loadAllData(boolean forceRefresh) {
        if(indexData == null || forceRefresh){
            loadIndexData();
        }
        if(bangumis == null || bangumis.size()==0){
            loadBangumiRecommendData("-1",10);
        }
    }

    private void initRecyclerView() {
        if (adapter == null) {
            swipeRefreshLayout.setOnRefreshListener(this);
            adapter = new BangumiAdapter(getActivity(), indexData ,bangumis);
            adapter.setItemClickListener(this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = adapter.getItemViewType(position);
                    if (type == BangumiAdapter.BANNER ||
                            type == BangumiAdapter.NAV ||
                            type == BangumiAdapter.SERIALIZING_HEAD ||
                            type == BangumiAdapter.SEASON_BANGUMI_HEAD ||
                            type == BangumiAdapter.BANGUMI_RECOMMEND_HEAD ||
                            type == BangumiAdapter.BANGUMI_RECOMMEND_ITEM ||
                            type == BangumiAdapter.LOAD_MORE) {
                        return 3;
                    } else {
                        return 1;
                    }
                }
            });
            recyclerView.addItemDecoration(new BangumiIndexItemDecoration(getActivity()));
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setOnLoadMoreLinstener(this);
        }
    }

    private void setIndexData() {
        adapter.setIndexPage(indexData);
        adapter.notifyDataSetChanged();
    }

    private void addBangumiRecommendData(List<IndexBangumiRecommend> date) {
        if (bangumis == null) {
            bangumis = new ArrayList<>();
            adapter.setBangumis(bangumis);
        }
        bangumis.addAll(date);
        recyclerView.setLoading(false);
        adapter.notifyDataSetChanged();
    }

    private void loadIndexData() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        indexCall = IMBilibiliApplication.getApplication().getApi()
                .getIndexPage(Constant.APPKEY, Constant.BUILD, Constant.MOBI_APP,
                        Constant.MOBI_APP, System.currentTimeMillis());
        indexCall.enqueue(new Callback<BiliBiliResultResponse<IndexPage>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<IndexPage>> call, Response<BiliBiliResultResponse<IndexPage>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body().getCode() == 0) {
                    indexData = response.body().getResult();
                    Collections.sort(indexData.getSerializing());
                    setIndexData();
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResultResponse<IndexPage>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadBangumiRecommendData(String cursor, int pageSize) {
        recommendCall = IMBilibiliApplication.getApplication().getApi().getBangumiRecommend(Constant.APPKEY, Constant.BUILD, cursor, Constant.MOBI_APP,
                pageSize, Constant.MOBI_APP, System.currentTimeMillis());
        recommendCall.enqueue(new Callback<BiliBiliResultResponse<List<IndexBangumiRecommend>>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<List<IndexBangumiRecommend>>> call, Response<BiliBiliResultResponse<List<IndexBangumiRecommend>>> response) {
                if (response.isSuccessful() && response.body().getCode() == 0) {
                    if (response.body().getResult().size() == 0) {
                        recyclerView.setLoadView("再怎么找也没有了", false);
                        recyclerView.setEnableLoadMore(false);
                    }
                    addBangumiRecommendData(response.body().getResult());
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResultResponse<List<IndexBangumiRecommend>>> call, Throwable t) {

            }
        });
    }

    @Override
    public String getTitle() {
        return "番剧";
    }

    @Override
    public void onRefresh() {
        if(bangumis!=null) {
            bangumis.clear();
        }
        recyclerView.setEnableLoadMore(true);
        loadAllData(true);
    }

    @Override
    public void onLoadMore() {
        if (bangumis == null || bangumis.size() == 0) {
            loadBangumiRecommendData("-1", 10);
        } else {
            loadBangumiRecommendData(bangumis.get(bangumis.size() - 1).getCursor(), 10);
        }
    }

    @Override
    public void onClick(int itemType, int position) {
        Intent intent = new Intent();
        if (itemType == BangumiAdapter.SERIALIZING_GRID_ITEM) {
            intent.putExtra(Constant.QUERY_SEASON_ID, indexData.getSerializing().get(position).getSeasonId());
            intent.setClass(getContext(), BangumiDetailActivity.class);
        } else if (itemType == BangumiAdapter.SEASON_BANGUMI_ITEM) {
            intent.putExtra(Constant.QUERY_SEASON_ID, indexData.getPrevious().getList().get(position).getSeasonId());
            intent.setClass(getContext(), BangumiDetailActivity.class);
        } else if (itemType == BangumiAdapter.BANGUMI_RECOMMEND_ITEM ||
                itemType == BangumiAdapter.BANNER) {
            String link = null;
            if(itemType == BangumiAdapter.BANNER){
                Banner banner = indexData.getAd().getHead().get(position);
                link = banner.getLink();
            }else {
                IndexBangumiRecommend bangumi = bangumis.get(position);
                link = bangumi.getLink();
            }
            if (link.contains("anime")) {
                String[] temp = link.split("anime/");
                intent.putExtra(Constant.QUERY_SEASON_ID, temp[temp.length - 1]);
                intent.setClass(getContext(), BangumiDetailActivity.class);
            } else {
                intent.putExtra(WebViewActivity.EXTRA_DATA, link);
                intent.setClass(getContext(), WebViewActivity.class);
            }
        } else if(itemType == BangumiAdapter.SEASON_BANGUMI_HEAD){
            intent.setClass(getContext(), SeasonGroupActivity.class);
        }else {
            // TODO: 2016/8/9
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        CallUtils.cancelCall(indexCall, recommendCall);
        super.onDestroy();
    }
}
