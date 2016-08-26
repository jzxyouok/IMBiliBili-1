package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BangumiDetail;
import com.lh.imbilibili.model.BangumiIndex;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.FeedbackData;
import com.lh.imbilibili.model.IndexBangumiRecommend;
import com.lh.imbilibili.model.IndexPage;
import com.lh.imbilibili.model.ReplyCount;
import com.lh.imbilibili.model.SeasonGroup;
import com.lh.imbilibili.model.SeasonRecommend;
import com.lh.imbilibili.model.SourceData;
import com.lh.imbilibili.model.Splash;
import com.lh.imbilibili.model.VideoPlayData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/7/5.
 */
public interface BilibiliApi {
    @GET(Constant.SPLASH_URL)
    Call<BilibiliDataResponse<Splash>> getSplash(@Query("plat") String plat,
                                                 @Query("build") String build,
                                                 @Query("channel") String channel,
                                                 @Query("width") String width,
                                                 @Query("height") String height);

    @GET(Constant.BANGUMI_RECOMMEND)
    Call<BiliBiliResultResponse<List<IndexBangumiRecommend>>> getBangumiRecommend(@Query(Constant.QUERY_APP_KEY) String appkey,
                                                                                  @Query(Constant.QUERY_BUILD) String build,
                                                                                  @Query("cursor") String cursor,
                                                                                  @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                                                  @Query("pagesize") int pagesize,
                                                                                  @Query(Constant.QUERY_PLATFORM) String platform,
                                                                                  @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.INDEX_PAGE)
    Call<BiliBiliResultResponse<IndexPage>> getIndexPage(@Query(Constant.QUERY_APP_KEY) String appKey,
                                                         @Query(Constant.QUERY_BUILD) String build,
                                                         @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                         @Query(Constant.QUERY_PLATFORM) String platform,
                                                         @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.BANGUMI_DETAIL)
    Call<BiliBiliResultResponse<BangumiDetail>> getBangumiDetail(@Query(Constant.QUERY_APP_KEY) String appKey,
                                                                 @Query(Constant.QUERY_BUILD) String build,
                                                                 @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                                 @Query(Constant.QUERY_PLATFORM) String platform,
                                                                 @Query(Constant.QUERY_SEASON_ID) String seasonId,
                                                                 @Query(Constant.QUERY_TS) long ts,
                                                                 @Query(Constant.QUERY_TYPE) String type);

    @GET(Constant.API_URL + Constant.FEEDBACK)
    Call<BilibiliDataResponse<FeedbackData>> getFeedback(@Query(Constant.QUERY_APP_KEY) String appKey,
                                                         @Query(Constant.QUERY_BUILD) String build,
                                                         @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                         @Query("nohot") int nohot,
                                                         @Query("oid") String oid,
                                                         @Query(Constant.QUERY_PLATFORM) String platform,
                                                         @Query("pn") int pn,
                                                         @Query("ps") int ps,
                                                         @Query("sort") int sort,
                                                         @Query(Constant.QUERY_TYPE) int type);

    @GET(Constant.SEASON_RECOMMEND)
    Call<BiliBiliResultResponse<SeasonRecommend>> getSeasonRecommend(@Path(Constant.QUERY_SEASON_ID) String seasonId,
                                                                     @Query(Constant.QUERY_APP_KEY) String appKey,
                                                                     @Query(Constant.QUERY_BUILD) String build,
                                                                     @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                                     @Query(Constant.QUERY_PLATFORM) String platform,
                                                                     @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.API_URL + Constant.REPLY_COUNT)
    Call<BilibiliDataResponse<ReplyCount>> getReplyCount(@Query(Constant.QUERY_APP_KEY) String appKey,
                                                         @Query(Constant.QUERY_BUILD) String build,
                                                         @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                         @Query("oid") String oid,
                                                         @Query(Constant.QUERY_PLATFORM) String platform,
                                                         @Query(Constant.QUERY_TYPE) int type);

    @GET(Constant.GET_SOURCES)
    Call<BiliBiliResultResponse<List<SourceData>>> getSource(@Query(Constant.QUERY_APP_KEY) String appKey,
                                                             @Query(Constant.QUERY_BUILD) String build,
                                                             @Query(Constant.QUERY_EPISODE_ID) String episodeId,
                                                             @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                             @Query(Constant.QUERY_PLATFORM) String platform,
                                                             @Query(Constant.QUERY_TS) long ts);


    @GET(Constant.INTERFACE_URL + Constant.PLAY_URL)
    Call<VideoPlayData> getPlayData(@Query("_aid") String aid,
                                    @Query("_tid") int tid,
                                    @Query("_p") int p,
                                    @Query("_down") int down,
                                    @Query("cid") String cid,
                                    @Query("quality") int quality,
                                    @Query("otype") String otype,
                                    @Query(Constant.QUERY_APP_KEY) String appkey,
                                    @Query("type") String type);

    @GET(Constant.SEASON_GROUP)
    Call<BiliBiliResultResponse<List<SeasonGroup>>> getSeasonGroup(@Query(Constant.QUERY_APP_KEY) String appKey,
                                                                   @Query(Constant.QUERY_BUILD) String build,
                                                                   @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                                   @Query(Constant.QUERY_PLATFORM) String platform,
                                                                   @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.BANGUMI_INDEX)
    Call<BiliBiliResultResponse<BangumiIndex>> getBangumiIndex(@Query(Constant.QUERY_APP_KEY) String appKey,
                                                               @Query(Constant.QUERY_BUILD) String build,
                                                               @Query("index_sort") int indexSort,
                                                               @Query("index_type") int indexType,
                                                               @Query("initial") String initial,
                                                               @Query("is_finish") int isFinish,
                                                               @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                               @Query("page") int page,
                                                               @Query("page_size") int pageSize,
                                                               @Query(Constant.QUERY_PLATFORM) String platform,
                                                               @Query("quarter") int quarter,
                                                               @Query("start_year") int startYear,
                                                               @Query("tag_id") String tagId,
                                                               @Query(Constant.QUERY_TS) long ts,
                                                               @Query("update_period") int updatePeriod,
                                                               @Query("version") int version);
}
//    @GET(Constant.INTERFACE_URL+Constant.PLAY_URL)
//    Call<VideoPlayData> getPlayData(@Query(Constant.QUERY_PLATFORM)String platform,
//                                    @Query("_appver")String build,
//                                    @Query("_device")String device,
//                                    @Query("_aid")String aid,
//                                    @Query("_tid")int tid,
//                                    @Query("_p")int p,
//                                    @Query("_down")int down,
//                                    @Query("cid")String cid,
//                                    @Query("quality")int quality,
//                                    @Query("otype")String otype,
//                                    @Query(Constant.QUERY_APP_KEY)String appkey,
//                                    @Query("type")String type);
//}

