package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.FeedbackData;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.RetrofitHelper;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.feedbackfragment.FeedbackAdapter;
import com.lh.imbilibili.view.adapter.feedbackfragment.FeedbackItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/2.
 */

public class VideoDetailReplyFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreViewClickListener, LoadMoreRecyclerView.OnLoadMoreLinstener {

    private static final String EXTRA_ID = "id";

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private int mCurrentPage = 1;
    private Call<BilibiliDataResponse<FeedbackData>> mFeedbackCall;
    private FeedbackData mFeedbackData;
    private FeedbackAdapter mFeedbackAdapter;

    private String mId;

    private int mPageSize = 20;

    public static VideoDetailReplyFragment newInstance(String id) {
        VideoDetailReplyFragment fragment = new VideoDetailReplyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mId = getArguments().getString(EXTRA_ID);
        mFeedbackAdapter = new FeedbackAdapter(mFeedbackData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        FeedbackItemDecoration itemDecoration = new FeedbackItemDecoration(ContextCompat.getColor(getContext(), R.color.theme_color_dividing_line));
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mFeedbackAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        loadFeedbackData(mId, mCurrentPage);
    }

    private void loadFeedbackData(String id, int page) {
        mFeedbackCall = RetrofitHelper.getInstance().getReplyService().getFeedback(0, id, page, mPageSize, 0, 1);
        mFeedbackCall.enqueue(new Callback<BilibiliDataResponse<FeedbackData>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<FeedbackData>> call, Response<BilibiliDataResponse<FeedbackData>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().getCode() == 0) {
                    if (response.body().getData().getReplies().isEmpty()) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView("没有更多了", false);
                    }
                    if (mFeedbackData == null) {
                        mFeedbackData = response.body().getData();
                    } else {
                        mFeedbackData.getReplies().addAll(response.body().getData().getReplies());
                    }
                    mFeedbackAdapter.setFeedbackData(mFeedbackData);
                    mFeedbackAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<FeedbackData>> call, Throwable t) {
                mCurrentPage--;
                mRecyclerView.setEnableLoadMore(false);
                mRecyclerView.setLoadView("点击重试", false);
                mRecyclerView.setOnLoadMoreViewClickListener(VideoDetailReplyFragment.this);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mFeedbackCall);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_video_detail_reply;
    }

    @Override
    public String getTitle() {
        return "评论";
    }

    @Override
    public void onLoadMoreViewClick() {
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setLoadView("加载中", true);
        loadFeedbackData(mId, mCurrentPage);
        mCurrentPage++;
    }

    @Override
    public void onLoadMore() {
        mCurrentPage++;
        loadFeedbackData(mId, mCurrentPage);
    }
}
