package com.lh.imbilibili.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.usercenter.ViewPagerAdapter;
import com.lh.imbilibili.view.fragment.UserCenterHomeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/15.
 */

public class UserCenterActivity extends BaseActivity {

    private static final String EXTRA_ID = "id";
    private static final int PAGE_SIZE = 10;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.user_background)
    ImageView mIvBackground;
    @BindView(R.id.nick_name)
    TextView mTvNickName;
    @BindView(R.id.user_sex)
    ImageView mIvSex;
    @BindView(R.id.user_level)
    ImageView mIvLevel;
    @BindView(R.id.tv_follow_users)
    TextView mTvFollowUsers;
    @BindView(R.id.tv_fans)
    TextView mTvFans;
    @BindView(R.id.author_verified_layout)
    ViewGroup mAuthorLayout;
    @BindView(R.id.author_verified_text)
    TextView mAuthorText;
    @BindView(R.id.user_desc)
    TextView mTvUserDesc;
    @BindView(R.id.user_avatar)
    ImageView mIvUserAvator;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private String mId;
    private int[] mLevelImg = new int[]{R.drawable.ic_lv0_large,
            R.drawable.ic_lv1_large,
            R.drawable.ic_lv2_large,
            R.drawable.ic_lv3_large,
            R.drawable.ic_lv4_large,
            R.drawable.ic_lv5_large,
            R.drawable.ic_lv6_large,
            R.drawable.ic_lv7_large,
            R.drawable.ic_lv8_large,
            R.drawable.ic_lv9_large};
    private BaseFragment[] mFragments;

    private Call<BilibiliDataResponse<UserCenter>> mUserInfoCall;

    public static void startActivity(Context context, String id) {
        Intent intent = new Intent(context, UserCenterActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        ButterKnife.bind(this);
        StatusBarUtils.setCollapsingToolbarLayout(this, mToolbar, mAppBarLayout, mCollapsingToolbarLayout);
        mId = getIntent().getStringExtra(EXTRA_ID);
        Glide.with(this).load("file:///android_asset/ic_zone_background.png").centerCrop().into(mIvBackground);
        loadUserInfo();
    }

    private void loadUserInfo() {
        mUserInfoCall = RetrofitHelper.getInstance().getUserService().getUserSpaceInfo(PAGE_SIZE, System.currentTimeMillis(), mId);
        mUserInfoCall.enqueue(new Callback<BilibiliDataResponse<UserCenter>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<UserCenter>> call, Response<BilibiliDataResponse<UserCenter>> response) {
                if (response.body().isSuccess()) {
                    bindViewData(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<UserCenter>> call, Throwable t) {
                ToastUtils.showToast(UserCenterActivity.this, "加载失败", Toast.LENGTH_SHORT);
            }
        });
    }

    private void bindViewData(UserCenter userCenter) {
        if (!TextUtils.isEmpty(userCenter.getImages().getImgUrl())) {
            Glide.with(this).load(userCenter.getImages().getImgUrl()).centerCrop().into(mIvBackground);
        }
        Glide.with(this).load(userCenter.getCard().getFace()).transform(new CircleTransformation(getApplicationContext())).into(mIvUserAvator);
        mTvNickName.setText(userCenter.getCard().getName());
        mIvLevel.setImageResource(mLevelImg[userCenter.getCard().getLevelInfo().getCurrentLevel()]);
        switch (userCenter.getCard().getSex()) {
            case "男":
                mIvSex.setImageResource(R.drawable.ic_user_male_border);
                break;
            case "女":
                mIvSex.setImageResource(R.drawable.ic_user_female_border);
                break;
            default:
                mIvSex.setImageResource(R.drawable.ic_user_gay_border);
                break;
        }
        mTvFollowUsers.setText(StringUtils.formateNumber(userCenter.getCard().getAttention()));
        mTvFans.setText(StringUtils.formateNumber(userCenter.getCard().getFans()));
        if (userCenter.getCard().getOfficialVerify().getType() == 1 && !TextUtils.isEmpty(userCenter.getCard().getOfficialVerify().getDesc())) {
            mAuthorLayout.setVisibility(View.VISIBLE);
            mAuthorText.setText(userCenter.getCard().getOfficialVerify().getDesc());
        }
        if (TextUtils.isEmpty(userCenter.getCard().getSign())) {
            mTvUserDesc.setText("这个人懒死了，什么都没写");
        } else {
            mTvUserDesc.setText(userCenter.getCard().getSign());
        }
        mFragments = new BaseFragment[]{UserCenterHomeFragment.newInstance(userCenter)};
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mUserInfoCall);
    }
}
