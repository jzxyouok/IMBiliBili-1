package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.user.UserDetailInfo;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by liuhui on 2016/10/8.
 */

public interface AccountService {

    @GET(Constant.ACCOUNT_URL + Constant.MY_INFO)
    Call<UserDetailInfo> getUserDetailInfo();


}
