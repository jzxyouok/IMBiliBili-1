package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BangumiDetail;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.FeedbackData;
import com.lh.imbilibili.model.ReplyCount;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.StringUtils;
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
 * Created by home on 2016/8/2.
 */
public class FeedbackFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, LoadMoreRecyclerView.OnLoadMoreViewClickListener, View.OnClickListener {

    public static final String TAG = "FeedbackFragment";
    private final int pageSize = 20;
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.comment_count)
    TextView tvCommentCount;
    @BindView(R.id.choose_episode)
    TextView tvChooseEpisode;
    @BindView(R.id.feedback_list)
    LoadMoreRecyclerView recyclerView;
    private FeedbackData feedbackData;
    private BangumiDetail bangumiDetail;
    private int currentPage = 0;
    private FeedbackAdapter feedbackAdapter;

    private Call<BilibiliDataResponse<FeedbackData>> feedbackCall;
    private Call<BilibiliDataResponse<ReplyCount>> replyCountCall;

    private int selectPosition;
    private EpisodeFragment episodeFragment;

    public static FeedbackFragment newInstance(Bundle bundle) {
        FeedbackFragment feedbackFragment = new FeedbackFragment();
        feedbackFragment.setArguments(bundle);
        return feedbackFragment;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_feedback_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bangumiDetail = getArguments().getParcelable("data");
        selectPosition = getArguments().getInt("position", 0);
        initView();
        if (feedbackData == null) {
            tvTitle.setText(StringUtils.format("第%s话", bangumiDetail.getEpisodes().get(selectPosition).getIndex()));
            loadFeedbackData(bangumiDetail.getEpisodes().get(selectPosition).getAvId(), 0);
            loadReplyCount(bangumiDetail.getEpisodes().get(selectPosition).getAvId());
        }
    }

    private void loadReplyCount(String id) {
        replyCountCall = RetrofitHelper.getInstance().getReplyService().getReplyCount(id, 1);
        replyCountCall.enqueue(new Callback<BilibiliDataResponse<ReplyCount>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<ReplyCount>> call, Response<BilibiliDataResponse<ReplyCount>> response) {
                if (response.body().getCode() == 0) {
                    tvCommentCount.setText(StringUtils.format("%d楼", response.body().getData().getCount()));
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<ReplyCount>> call, Throwable t) {

            }
        });
    }

    private void loadFeedbackData(String id, int page) {
        feedbackCall = RetrofitHelper.getInstance().getReplyService().getFeedback(0, id, page, pageSize, 0, 1);
        feedbackCall.enqueue(new Callback<BilibiliDataResponse<FeedbackData>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<FeedbackData>> call, Response<BilibiliDataResponse<FeedbackData>> response) {
                if (response.body().getCode() == 0) {
                    if (feedbackData == null) {
                        feedbackData = response.body().getData();
                    } else {
                        feedbackData.getReplies().addAll(response.body().getData().getReplies());
                        recyclerView.setLoading(false);
                    }
                    feedbackAdapter.setFeedbackData(feedbackData);
                    feedbackAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<FeedbackData>> call, Throwable t) {
                currentPage--;
                recyclerView.setEnableLoadMore(false);
                recyclerView.setLoadView("点击重试", false);
                recyclerView.setOnLoadMoreViewClickListener(FeedbackFragment.this);
            }
        });
    }

    private void initView() {
        feedbackAdapter = new FeedbackAdapter(feedbackData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        FeedbackItemDecoration itemDecoration = new FeedbackItemDecoration(ContextCompat.getColor(getContext(), R.color.theme_color_dividing_line));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(feedbackAdapter);
        recyclerView.setOnLoadMoreLinstener(this);
        tvChooseEpisode.setOnClickListener(this);
    }

    private void showEpChooseDialog() {
        if (episodeFragment == null) {
            episodeFragment = new EpisodeFragment();
            episodeFragment.setCancelable(true);
        }
        episodeFragment.setArguments(getArguments());
        episodeFragment.show(getFragmentManager(), EpisodeFragment.TAG);
    }

    @Override
    public void onDestroy() {
        CallUtils.cancelCall(feedbackCall, replyCountCall);
        super.onDestroy();
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadFeedbackData(bangumiDetail.getEpisodes().get(selectPosition).getAvId(), currentPage);
    }

    @Override
    public void onLoadMoreViewClick() {
        recyclerView.setEnableLoadMore(true);
        recyclerView.setLoadView("加载中", true);
        loadFeedbackData(bangumiDetail.getEpisodes().get(selectPosition).getAvId(), currentPage);
        currentPage++;
    }

    @Override
    public void onClick(View v) {
        showEpChooseDialog();
    }

    public void onEpisodeSelect(int position) {
        selectPosition = position;
        feedbackData = null;
        currentPage = 0;
        tvTitle.setText(StringUtils.format("第%s话", bangumiDetail.getEpisodes().get(position).getIndex()));
        loadFeedbackData(bangumiDetail.getEpisodes().get(position).getAvId(), currentPage);
        loadReplyCount(bangumiDetail.getEpisodes().get(position).getAvId());
        if (episodeFragment != null) {
            episodeFragment.dismiss();
        }
    }
}
