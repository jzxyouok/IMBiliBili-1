package com.lh.imbilibili.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.attention.FollowBangumi;
import com.lh.imbilibili.model.attention.FollowBangumiResponse;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.adapter.attention.AttentionBangumiRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.attention.LineItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/14.
 * 显示用户的追番列表
 */

public class FollowBangumiActivity extends BaseActivity implements LoadMoreRecyclerView.OnLoadMoreLinstener, AttentionBangumiRecyclerViewAdapter.OnItemClickListener {

    private static final int PAGE_SIZE = 20;

    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private int mCurrentPage;
    private AttentionBangumiRecyclerViewAdapter mAdapter;


    private Call<FollowBangumiResponse<List<FollowBangumi>>> mConcernedBangumiCall;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FollowBangumiActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folow_bangumi);
        ButterKnife.bind(this);
        mCurrentPage = 1;
        StatusBarUtils.setSimpleToolbarLayout(this, mToolbar);
        mToolbar.setTitle("追番");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initRecyclerView();
        loadConcernedBangumi();
    }

    private void initRecyclerView() {
        mAdapter = new AttentionBangumiRecyclerViewAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new LineItemDecoration(ContextCompat.getColor(this, R.color.gray)));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnItemClickListener(this);
    }

    private void loadConcernedBangumi() {
        mConcernedBangumiCall = RetrofitHelper.getInstance().getAttentionService().getConcernedBangumi(mCurrentPage, PAGE_SIZE, System.currentTimeMillis());
        mConcernedBangumiCall.enqueue(new Callback<FollowBangumiResponse<List<FollowBangumi>>>() {
            @Override
            public void onResponse(Call<FollowBangumiResponse<List<FollowBangumi>>> call, Response<FollowBangumiResponse<List<FollowBangumi>>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().isSuccess()) {
                    if (response.body().getResult().isEmpty()) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView(R.string.no_data_tips, false);
                    } else {
                        int startPosition = mAdapter.getItemCount();
                        mAdapter.addBangumi(response.body().getResult());
                        if (mCurrentPage == 1) {
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.notifyItemRangeInserted(startPosition, response.body().getResult().size());
                        }
                        mCurrentPage++;
                    }
                }
            }

            @Override
            public void onFailure(Call<FollowBangumiResponse<List<FollowBangumi>>> call, Throwable t) {
                mRecyclerView.setLoading(false);
                ToastUtils.showToast(FollowBangumiActivity.this, R.string.load_error, Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onLoadMore() {
        loadConcernedBangumi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mConcernedBangumiCall);
    }

    @Override
    public void onItemClick(String seasonId) {
        BangumiDetailActivity.startActivity(this, seasonId);
    }
}
