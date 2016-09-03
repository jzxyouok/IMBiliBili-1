package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BangumiIndex;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.activity.BangumiDetailActivity;
import com.lh.imbilibili.view.adapter.GridRecyclerViewItemDecoration;
import com.lh.imbilibili.view.adapter.bangumiindexfragment.BangumiIndexAdapter;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by home on 2016/8/11.
 */
public class BangumiIndexFragment extends BaseFragment implements LoadMoreRecyclerView.onLoadMoreLinstener, BangumiIndexAdapter.OnBangumiItemClickListener, View.OnClickListener {
    public static final String TAG = "BangumiIndexFragment";

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mLoadMoreRecyclerView;
    @BindView(R.id.iv_hit_up)
    ImageView mIvHitUp;
    @BindView(R.id.iv_hit_down)
    ImageView mIvHitDown;
    @BindView(R.id.btn_sort_hit)
    TextView mTvSortHit;
    @BindView(R.id.iv_recent_up)
    ImageView mIvRecentUp;
    @BindView(R.id.iv_recent_down)
    ImageView mIvRecentDown;
    @BindView(R.id.btn_sort_recent)
    TextView mTvSortRecent;
    @BindView(R.id.iv_day_up)
    ImageView mIvDayUp;
    @BindView(R.id.iv_day_down)
    ImageView mIvDayDown;
    @BindView(R.id.btn_sort_day)
    TextView mTvSortDay;

    @BindView(R.id.btn_filter_type)
    TextView mTvFilterType;
    @BindView(R.id.btn_filter_style)
    TextView mTvFilterStyle;
    @BindView(R.id.btn_filter_status)
    TextView mTvFilterStatus;
    @BindView(R.id.btn_filter_drawer)
    TextView mTvFilterDrawer;

    private BangumiIndexAdapter mAdapter;

    private BangumiIndex mBangumiIndex;
    private Call<BiliBiliResultResponse<BangumiIndex>> mBangumiIndexCall;

    private int mCurrentPage;
    private int mYear;
    private int mQuarter;

    private int mIndexSort = 0;//0降序 1升序
    private int mIndexSortType = 1;//0更新时间 1追番人数 2开播时间
    private int mCurrentSelectFilter = -1;

    private TextView[] mTvSortButtons;
    private ImageView[] mIvUps;
    private ImageView[] mIvDowns;
    private TextView[] mTvFliterButtons;

    public static BangumiIndexFragment newInstance(int year, int quarter) {
        BangumiIndexFragment fragment = new BangumiIndexFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("year", year);
        bundle.putInt("quarter", quarter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bangumi_index, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StatusBarUtils.setDrawerToolbarLayout(getActivity(),(Toolbar) getActivity().findViewById(R.id.nav_top_bar),(ViewGroup) getActivity().findViewById(R.id.drawer));
        mCurrentPage = 1;
        mYear = getArguments().getInt("year");
        mQuarter = getArguments().getInt("quarter");
        mTvSortButtons = new TextView[]{mTvSortRecent, mTvSortHit, mTvSortDay};
        mIvUps = new ImageView[]{mIvRecentUp, mIvHitUp, mIvDayUp};
        mIvDowns = new ImageView[]{mIvRecentDown, mIvHitDown, mIvDayDown};
        mTvFliterButtons = new TextView[]{mTvFilterType, mTvFilterStyle, mTvFilterStatus, mTvFilterDrawer};
        initSortAndFliterView();
        initRecyclerView();
        loadDataAccordingFlilter();
    }

    private void initSortAndFliterView() {
        for (TextView mTvSortButton : mTvSortButtons) {
            mTvSortButton.setOnClickListener(this);
        }
        for (TextView mTvFliterButton : mTvFliterButtons) {
            mTvFliterButton.setOnClickListener(this);
        }
    }

    private void initRecyclerView() {
        mAdapter = new BangumiIndexAdapter(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getItemViewType(position) == LoadMoreRecyclerView.LoadMoreAdapter.LOAD_MORE) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        GridRecyclerViewItemDecoration itemDecoration = new GridRecyclerViewItemDecoration(getContext(), true);
        mLoadMoreRecyclerView.setLayoutManager(gridLayoutManager);
        mLoadMoreRecyclerView.addItemDecoration(itemDecoration);
        mLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreRecyclerView.setOnLoadMoreLinstener(this);
        mLoadMoreRecyclerView.setEnableLoadMore(true);
        mAdapter.setOnBangumiItemClickListener(this);
    }

    private void loadDataAccordingFlilter() {
        int colorPrimary = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        int colorBlack = ContextCompat.getColor(getContext(), R.color.black);
        for (int i = 0; i < mTvSortButtons.length; i++) {
            if (i == mIndexSortType) {
                mTvSortButtons[i].setTextColor(colorPrimary);
            } else {
                mTvSortButtons[i].setTextColor(colorBlack);
            }
        }
        for (int i = 0; i < mIvDowns.length; i++) {
            if (i == mIndexSortType) {
                (mIndexSort == 0 ? mIvDowns[i] : mIvUps[i]).setColorFilter(colorPrimary);
                (mIndexSort == 1 ? mIvDowns[i] : mIvUps[i]).setColorFilter(null);
            } else {
                (mIndexSort == 0 ? mIvDowns[i] : mIvUps[i]).setColorFilter(null);
                (mIndexSort == 1 ? mIvDowns[i] : mIvUps[i]).setColorFilter(null);
            }
        }
        for (int i = 0; i < mTvFliterButtons.length; i++) {
            if (i == mCurrentSelectFilter) {
                mTvFliterButtons[i].setTextColor(colorPrimary);
            } else {
                mTvFliterButtons[i].setTextColor(colorBlack);
            }
        }
        loadData(mIndexSort, mIndexSortType, 0, mCurrentPage, 30, mQuarter, mYear, "0", 0, 0);
    }


    /**
     * @param indexSort    0降序 1升序
     * @param indexType    0更新时间 1追番人数 2开播时间
     * @param isFinish     是否完结 0全部 1完结 2连载中
     * @param page         当前页码
     * @param pageSize     每页所包含的内容数量
     * @param quarter      季度 0：1, 1：4, 2：7, 3：10;
     * @param startYear    开始年份
     * @param tagId        标签Id
     * @param updatePeriod 默认0
     * @param version      类型：0全部 1Tv版 2OVA·OAD 3剧场版 4其他
     */
    private void loadData(int indexSort, int indexType, int isFinish, int page, int pageSize, int quarter, int startYear, final String tagId, int updatePeriod, int version) {
        mBangumiIndexCall = IMBilibiliApplication.getApplication().getApi().getBangumiIndex(Constant.APPKEY, Constant.BUILD,
                indexSort, indexType, "", isFinish, Constant.MOBI_APP, page, pageSize, Constant.PLATFORM,
                quarter, startYear, tagId, System.currentTimeMillis(), updatePeriod, version);
        mBangumiIndexCall.enqueue(new Callback<BiliBiliResultResponse<BangumiIndex>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<BangumiIndex>> call, Response<BiliBiliResultResponse<BangumiIndex>> response) {
                if (response.body().getCode() == 0) {
                    mBangumiIndex = response.body().getResult();
                    mAdapter.addBangumis(mBangumiIndex.getList());
                    mAdapter.notifyDataSetChanged();
                    mLoadMoreRecyclerView.setLoading(false);
                    if (mBangumiIndex.getPages().equals(mCurrentPage + "")) {
                        mLoadMoreRecyclerView.setEnableLoadMore(false);
                        mLoadMoreRecyclerView.setLoadView("没有更多了", false);
                    } else {
                        mLoadMoreRecyclerView.setEnableLoadMore(true);
                        mLoadMoreRecyclerView.setLoadView("加载中...", true);
                    }
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResultResponse<BangumiIndex>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onLoadMore() {
        mCurrentPage++;
        loadDataAccordingFlilter();
//        loadData(0, 1, 0, mCurrentPage, 30, mQuarter, mYear, "0", 0, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mBangumiIndexCall);
    }

    @Override
    public void onBangumiClick(BangumiIndexAdapter.BangumiHolder holder) {
        BangumiDetailActivity.startActivity(getContext(), holder.getSeasonId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sort_hit:
                if (mIndexSortType != 1) {
                    mIndexSort = 0;
                } else {
                    mIndexSort = 1 - mIndexSort;
                }
                mIndexSortType = 1;
                mCurrentPage = 1;
                mAdapter.setIndexSortType(mIndexSortType);
                mAdapter.clear();
                loadDataAccordingFlilter();
                break;
            case R.id.btn_sort_recent:
                if (mIndexSortType != 0) {
                    mIndexSort = 0;
                } else {
                    mIndexSort = 1 - mIndexSort;
                }
                mIndexSortType = 0;
                mCurrentPage = 1;
                mAdapter.setIndexSortType(mIndexSortType);
                mAdapter.clear();
                loadDataAccordingFlilter();
                break;
            case R.id.btn_sort_day:
                if (mIndexSortType != 2) {
                    mIndexSort = 0;
                } else {
                    mIndexSort = 1 - mIndexSort;
                }
                mIndexSortType = 2;
                mCurrentPage = 1;
                mAdapter.setIndexSortType(mIndexSortType);
                mAdapter.clear();
                loadDataAccordingFlilter();
                break;
        }
    }
}
