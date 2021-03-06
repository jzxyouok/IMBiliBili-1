package com.lh.imbilibili.view.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.user.UserDetailInfo;
import com.lh.imbilibili.model.user.UserResponse;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.fragment.HistoryRecordFragment;
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
    private View mDrawerProfileLayout;
    private TextView mTvLevel;
    private TextView mTvMemberState;
    private TextView mTvCoinCount;

    private Call<UserDetailInfo> mUserInfoCall;
    private UserDetailInfo mUserDetailInfo;
    private boolean isShowHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BusUtils.getBus().register(this);
        View headView = mDrawer.getHeaderView(0);
        mDrawerProfileLayout = headView.findViewById(R.id.drawer_profile_layout);
        mIvAvatar = (ImageView) headView.findViewById(R.id.user_avatar);
        mTvNickName = (TextView) headView.findViewById(R.id.user_nick_text);
        mTvLevel = (TextView) headView.findViewById(R.id.level);
        mTvMemberState = (TextView) headView.findViewById(R.id.member_status);
        mTvCoinCount = (TextView) headView.findViewById(R.id.user_coin_count);
        initView();
        switchFragment(0);
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDrawerProfileLayout.setPadding(0, StatusBarUtils.getStatusBarHeight(this), 0, 0);
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
        mDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_home:
                        switchFragment(0);
                        break;
                    case R.id.item_history:
                        switchFragment(1);
                        break;
                }
                closeDrawer();
                return true;
            }
        });
    }

    private void switchFragment(int index) {
        Fragment fragment;
        String tag;
        isShowHome = index == 0;
        switch (index) {
            case 0:
                tag = MainFragment.TAG;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = MainFragment.newInstance();
                }
                break;
            case 1:
                tag = HistoryRecordFragment.TAG;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = HistoryRecordFragment.newInstance();
                }
                break;
            default:
                return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, tag).commit();
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
                } else {
                    UserCenterActivity.startActivity(this, UserManagerUtils.getInstance().getCurrentUser().getMid(), 0);
                }
                break;
        }
    }

    @Subscribe
    public void loadUserInfo(UserResponse response) {
        mUserInfoCall = RetrofitHelper.getInstance().getUserService().getUserDetailInfo();
        mUserInfoCall.enqueue(new Callback<UserDetailInfo>() {
            @Override
            public void onResponse(Call<UserDetailInfo> call, Response<UserDetailInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mUserDetailInfo = response.body();
                    UserManagerUtils.getInstance().setUserDetailInfo(mUserDetailInfo);
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
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawer)) {
            mDrawerLayout.closeDrawer(mDrawer);
            return;
        }
        if (!isShowHome) {
            mDrawer.setCheckedItem(R.id.item_home);
            switchFragment(0);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mUserInfoCall);
        BusUtils.getBus().unregister(this);
    }
}
