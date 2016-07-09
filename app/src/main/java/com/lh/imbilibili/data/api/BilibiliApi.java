package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.Bangumi;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.IndexPage;
import com.lh.imbilibili.model.Splash;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/7/5.
 */
public interface BilibiliApi {
    @GET(Constant.SPLASH_URL)
    Call<BilibiliDataResponse<Splash>> getSplash(@Query("plat")String plat,
                                                 @Query("build")String build,
                                                 @Query("channel")String channel,
                                                 @Query("width")String width,
                                                 @Query("height")String height);

    @GET(Constant.BANGUMI_RECOMMEND)
    Call<BiliBiliResultResponse<List<Bangumi>>> getBangumiRecommend(@Query(Constant.QUERY_APP_KEY)String appkey,
                                                                             @Query(Constant.QUERY_BUILD)String build,
                                                                             @Query("cursor")String cursor,
                                                                             @Query(Constant.QUERY_MOBI_APP)String mobiApp,
                                                                             @Query("pagesize")String pagesize,
                                                                             @Query(Constant.QUERY_PLATFORM)String platform,
                                                                             @Query(Constant.QUERY_TS)String ts);
    //@Query(Constant.QUERY_ACCESS_KEY)String accessKey,
    @GET(Constant.INDEX_PAGE)
    Call<BiliBiliResultResponse<IndexPage>> getIndexPage(@Query(Constant.QUERY_APP_KEY)String appKey,
                                                       @Query(Constant.QUERY_BUILD)String build,
                                                       @Query(Constant.QUERY_MOBI_APP)String mobiApp,
                                                       @Query(Constant.QUERY_PLATFORM)String platform,
                                                       @Query(Constant.QUERY_TS)String ts);
}
