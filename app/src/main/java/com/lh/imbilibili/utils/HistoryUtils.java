package com.lh.imbilibili.utils;

import com.lh.imbilibili.model.BilibiliDataResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/8.
 */

public class HistoryUtils {
    public static void addHistory(String id) {

        RetrofitHelper.getInstance().getHistoryService().addHistory(id).enqueue(new Callback<BilibiliDataResponse>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse> call, Response<BilibiliDataResponse> response) {
                System.out.println("history:" + response.message());
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse> call, Throwable t) {

            }
        });
    }
}
