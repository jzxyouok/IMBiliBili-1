package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by liuhui on 2016/10/8.
 */

public interface HistoryService {
    @POST(Constant.API_URL + Constant.HISTORY_ADD)
    @FormUrlEncoded
    @Headers({"Content-Type: application/x-www-form-urlencoded; charset=UTF-8"})
    Call<BilibiliDataResponse> addHistory(@Field("aid") String aid);
}
