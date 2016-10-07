package com.lh.imbilibili.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.VideoDetail;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.DisableableAppBarLayoutBehavior;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.videodetailactivity.ViewPagerAdapter;
import com.lh.imbilibili.view.fragment.VideoDetailInfoFragment;
import com.lh.imbilibili.view.fragment.VideoDetailReplyFragment;
import com.lh.imbilibili.view.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/2.
 */

public class VideoDetailActivity extends BaseActivity implements VideoFragment.OnVideoFragmentStateListener {

    public static final String ACTION_NOTIFY_INFO = "com.lh.imbilibili.view.activity.VideoDetailActivity:info";
    private static final String EXTRA_AID = "aid";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.video_pre_view_layout)
    ViewGroup mPreViewLayout;
    @BindView(R.id.video_pre_view)
    ImageView mIvPreView;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.title_layout)
    ViewGroup mTitleLayout;
    @BindView(R.id.tv_player)
    TextView mTvPlayer;
    @BindView(R.id.video_view_container)
    FrameLayout mVideoContainer;

    private ViewPagerAdapter mAdapter;

    private String mAid;
    private VideoDetail mVideoDetail;
    private List<BaseFragment> mFragments;
    private VideoFragment mVideoFragment;

    private boolean mIsFabShow;
    private boolean mIsAppLayoutDisable;
    private boolean mIsFullScreen;
    private boolean mIsInitLayout;
    private int mVideoViewHeight;
    private int mCurrentSelectVideoPage;

    private Call<BilibiliDataResponse<VideoDetail>> mLoadVideoDetailCall;

    private LocalBroadcastManager mLocalBroadcastManager;

    public static void startActivity(Context context, String aid) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(EXTRA_AID, aid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        mAid = getIntent().getStringExtra(EXTRA_AID);
        ButterKnife.bind(this);
        StatusBarUtils.setCollapsingToolbarLayout(this, mToolbar, mAppBarLayout, mCollapsingToolbarLayout);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mIsFullScreen = false;
        mIsFabShow = true;
        mIsAppLayoutDisable = false;
        mIsInitLayout = false;
        mCurrentSelectVideoPage = 0;
        initView();
        loadVideoDetail();
    }

    private void initView() {
        mToolbar.setTitle(StringUtils.format("av%s", mAid));
        mCollapsingToolbarLayout.setTitleEnabled(false);
        mFloatingActionButton.setEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initVideoView(mCurrentSelectVideoPage);
            }
        });
        mTvPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initVideoView(mCurrentSelectVideoPage);
            }
        });
        mTvPlayer.setEnabled(false);
        mFragments = new ArrayList<>();
        mFragments.add(VideoDetailInfoFragment.newInstance());
        mFragments.add(VideoDetailReplyFragment.newInstance(mAid));
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(Color.BLACK, ContextCompat.getColor(this, R.color.colorPrimary));
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (!mIsAppLayoutDisable) {
                    if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                        mTitleLayout.setVisibility(View.VISIBLE);
                        mToolbar.setTitle("");
                        hidFab();
                    } else if (Math.abs(verticalOffset) > 0) {
                        mTitleLayout.setVisibility(View.GONE);
                        mToolbar.setTitle(StringUtils.format("av%s", mAid));
                        showFab();
                    }
                }
            }
        });
    }

    private void loadVideoDetail() {
        mLoadVideoDetailCall = IMBilibiliApplication.getApplication().getApi().getVideoDetail(mAid, Constant.APPKEY, Constant.BUILD, Constant.MOBI_APP, Constant.PLAT, Constant.PLATFORM, System.currentTimeMillis());
        mLoadVideoDetailCall.enqueue(new Callback<BilibiliDataResponse<VideoDetail>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<VideoDetail>> call, Response<BilibiliDataResponse<VideoDetail>> response) {
                if (response.body().getCode() == 0) {
                    mVideoDetail = response.body().getData();
                    sendVideoDetailInfo(mVideoDetail);
                    bindViewData();
                } else {
                    mIvPreView.setImageResource(R.drawable.img_tips_error_not_foud);
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<VideoDetail>> call, Throwable t) {
                ToastUtils.showToast(VideoDetailActivity.this, "加载失败", Toast.LENGTH_SHORT);
                mIvPreView.setImageResource(R.drawable.img_tips_error_not_foud);
            }
        });
    }

    private void sendVideoDetailInfo(VideoDetail detail) {
        Intent intent = new Intent(ACTION_NOTIFY_INFO);
        intent.putExtra(VideoDetailInfoFragment.EXTRA_DATA, detail);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private void bindViewData() {
        mFloatingActionButton.setEnabled(true);
        mTvPlayer.setEnabled(true);
        Glide.with(this).load(mVideoDetail.getPic()).centerCrop().into(mIvPreView);
    }

    private void showFab() {
        if (!mIsFabShow) {
            mIsFabShow = true;
            mFloatingActionButton.setClickable(true);
            mFloatingActionButton.animate().scaleX(1).scaleY(1).setDuration(500).start();
        }
    }

    private void hidFab() {
        if (mIsFabShow) {
            mIsFabShow = false;
            mFloatingActionButton.setClickable(false);
            mFloatingActionButton.animate().scaleX(0).scaleY(0).setDuration(500).start();
        }
    }

    public void changeVideoPage(int page) {
        if (mCurrentSelectVideoPage != page || mVideoFragment == null) {
            mCurrentSelectVideoPage = page;
            if (mVideoFragment == null) {
                initVideoView(mCurrentSelectVideoPage);
            } else {
                mVideoFragment.changeVideo(mAid, mVideoDetail.getPages().get(page).getCid() + "", mVideoDetail.getTitle());
            }
        }
    }

    private void initVideoView(final int page) {
        initPlayerLayout();
        mVideoFragment = VideoFragment.newInstance(mAid, mVideoDetail.getPages().get(page).getCid() + "", mVideoDetail.getTitle());
        mVideoContainer.setVisibility(View.VISIBLE);
        mVideoFragment.setOnFullScreemButtonClick(VideoDetailActivity.this);
        getSupportFragmentManager().beginTransaction().replace(R.id.video_view_container, mVideoFragment).commit();
    }

    private void initPlayerLayout() {
        if (mIsInitLayout) {
            return;
        }
        mIsInitLayout = true;
        for (int i = 0; i < mFragments.size(); i++) {
            if (mFragments.get(i) instanceof OnVideoStartPlayingListener) {
                ((OnVideoStartPlayingListener) mFragments.get(i)).onVideoStart();
            }
        }
        mIsAppLayoutDisable = true;
        mAppBarLayout.setExpanded(true);
        hidFab();
        mPreViewLayout.setVisibility(View.INVISIBLE);
        mToolbar.setVisibility(View.INVISIBLE);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        DisableableAppBarLayoutBehavior behavior = (DisableableAppBarLayoutBehavior) params.getBehavior();
        behavior.setEnableScroll(false);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                CoordinatorLayout.LayoutParams vparams = (CoordinatorLayout.LayoutParams) mViewPager.getLayoutParams();
                vparams.setBehavior(null);
                vparams.topMargin = mAppBarLayout.getHeight();
                mViewPager.setLayoutParams(vparams);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mLoadVideoDetailCall);
    }

    @Override
    public void onFullScreenClick() {
        if (!mIsFullScreen) {
            mIsFullScreen = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mIsFullScreen = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onMediaControlBarVisibleChanged(boolean isShow) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (isShow && !mIsFullScreen) {
            mToolbar.setVisibility(View.VISIBLE);
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mToolbar.setVisibility(View.GONE);
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsFullScreen) {
            fullScreen();
        } else {
            exitFullScreen();
        }
    }

    private void fullScreen() {
        mViewPager.setVisibility(View.GONE);
        mAppBarLayout.setVisibility(View.GONE);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mVideoContainer.getLayoutParams();
        mVideoViewHeight = params.height;
        params.height = CoordinatorLayout.LayoutParams.MATCH_PARENT;
        params.width = CoordinatorLayout.LayoutParams.MATCH_PARENT;
        mVideoContainer.setLayoutParams(params);
        mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void exitFullScreen() {
        mViewPager.setVisibility(View.VISIBLE);
        mAppBarLayout.setVisibility(View.VISIBLE);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mVideoContainer.getLayoutParams();
        params.height = mVideoViewHeight;
        params.width = CoordinatorLayout.LayoutParams.MATCH_PARENT;
        mVideoContainer.setLayoutParams(params);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsFullScreen) {
            mIsFullScreen = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            exitFullScreen();
        } else {
            super.onBackPressed();
        }
    }

    public interface OnVideoStartPlayingListener {
        void onVideoStart();
    }
}