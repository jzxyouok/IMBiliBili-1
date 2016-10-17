package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.model.user.UserDetailInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/8.
 */

public interface UserService {

    @GET(Constant.ACCOUNT_URL + Constant.MY_INFO)
    Call<UserDetailInfo> getUserDetailInfo();

    @GET(Constant.APP_URL + Constant.USER_SPACE)
    Call<BilibiliDataResponse<UserCenter>> getUserSpaceInfo(@Query("ps") int ps,
                                                            @Query("ts") long ts,
                                                            @Query("vmid") String mid);
}
