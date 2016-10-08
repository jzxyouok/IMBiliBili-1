package com.lh.imbilibili.view.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserDetailInfo;
import com.lh.imbilibili.model.user.UserResponse;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.RetrofitHelper;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.fragment.MainFragment;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements IDrawerLayoutActivity, View.OnClickListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer)
    NavigationView mDrawer;

    private ImageView mIvAvatar;
    private TextView mTvNickName;
    //    private ImageView mIvLoginBg;
    private TextView mTvLevel;
    private TextView mTvMemberState;
    private TextView mTvCoinCount;

    private Call<UserDetailInfo> mUserInfoCall;
    private UserDetailInfo mUserDetailInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BusUtils.getBus().register(this);
        View headView = mDrawer.getHeaderView(0);
        mIvAvatar = (ImageView) headView.findViewById(R.id.user_avatar);
        mTvNickName = (TextView) headView.findViewById(R.id.user_nick_text);
        mTvLevel = (TextView) headView.findViewById(R.id.level);
        mTvMemberState = (TextView) headView.findViewById(R.id.member_status);
        mTvCoinCount = (TextView) headView.findViewById(R.id.user_coin_count);
//        mIvLoginBg = (ImageView) mDrawer.getHeaderView(0).findViewById(R.id.login_bg);
        initView();
        initFragmentView();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDrawer.getHeaderView(0).setPadding(0, StatusBarUtils.getStatusBarHeight(this), 0, 0);
        }
        mIvAvatar.setOnClickListener(this);
        mTvNickName.setOnClickListener(this);
        if (UserManagerUtils.getInstance().getCurrentUser() != null) {
            loadUserInfo(null);
        } else {
            mIvAvatar.setImageResource(R.drawable.bili_default_avatar);
            mTvNickName.setText("点击头像登陆");
            mTvLevel.setVisibility(View.GONE);
        }
    }

    private void initFragmentView() {
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.feedback_container, mainFragment, MainFragment.TAG).commit();
    }

    @Override
    public void openDrawer() {
        mDrawerLayout.openDrawer(mDrawer);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mDrawer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_avatar:
            case R.id.user_nick_text:
                if (UserManagerUtils.getInstance().getCurrentUser() == null) {
                    LoginActivity.startActivity(this);
                }
                break;
        }
    }

    @Subscribe
    public void loadUserInfo(UserResponse response) {
        System.out.println("loadUserInfo");
        mUserInfoCall = RetrofitHelper.getInstance().getAccountService().getUserDetailInfo();
        mUserInfoCall.enqueue(new Callback<UserDetailInfo>() {
            @Override
            public void onResponse(Call<UserDetailInfo> call, Response<UserDetailInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mUserDetailInfo = response.body();
                    BusUtils.getBus().post(mUserDetailInfo);
                    bindUserInfoViewWithData();
                }
            }

            @Override
            public void onFailure(Call<UserDetailInfo> call, Throwable t) {
                ToastUtils.showToast(getApplicationContext(), "加载用户信息失败", Toast.LENGTH_SHORT);
            }
        });
    }

    private void bindUserInfoViewWithData() {
        Glide.with(this).load(mUserDetailInfo.getFace()).transform(new CircleTransformation(getApplicationContext())).into(mIvAvatar);
        mTvNickName.setText(mUserDetailInfo.getUname());
        mTvLevel.setVisibility(View.VISIBLE);
        mTvLevel.setText(StringUtils.format("LV%d", mUserDetailInfo.getLevelInfo().getCurrentLevel()));
        if (mUserDetailInfo.getIdentification() == 0) {
            mTvMemberState.setVisibility(View.VISIBLE);
        } else {
            mTvMemberState.setVisibility(View.GONE);
        }
        mTvCoinCount.setText(StringUtils.format("硬币 : %d", mUserDetailInfo.getCoins()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mUserInfoCall);
        BusUtils.getBus().unregister(this);
    }
}
