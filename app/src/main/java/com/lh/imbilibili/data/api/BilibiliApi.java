package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BangumiDetail;
import com.lh.imbilibili.model.BangumiIndex;
import com.lh.imbilibili.model.BangumiIndexCond;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.FeedbackData;
import com.lh.imbilibili.model.IndexBangumiRecommend;
import com.lh.imbilibili.model.IndexPage;
import com.lh.imbilibili.model.PartionHome;
import com.lh.imbilibili.model.PartionVideo;
import com.lh.imbilibili.model.ReplyCount;
import com.lh.imbilibili.model.SeasonGroup;
import com.lh.imbilibili.model.SeasonRecommend;
import com.lh.imbilibili.model.SourceData;
import com.lh.imbilibili.model.Splash;
import com.lh.imbilibili.model.VideoDetail;
import com.lh.imbilibili.model.VideoPlayData;
import com.lh.imbilibili.model.search.BangumiSearchResult;
import com.lh.imbilibili.model.search.SearchResult;
import com.lh.imbilibili.model.search.UpSearchResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/7/5.
 * biliapi
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
    Call<VideoPlayData> getPlayData(@Query(Constant.QUERY_PLATFORM) String platform,
                                    @Query("_appver") String build,
                                    @Query("_device") String device,
                                    @Query("_aid") String aid,
                                    @Query("_tid") int tid,
                                    @Query("_p") int p,
                                    @Query("_down") int down,
                                    @Query("cid") String cid,
                                    @Query("quality") int quality,
                                    @Query("otype") String otype,
                                    @Query(Constant.QUERY_APP_KEY) String appkey);


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
                                                               @Query("is_finish") String isFinish,
                                                               @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                               @Query("page") int page,
                                                               @Query("page_size") int pageSize,
                                                               @Query(Constant.QUERY_PLATFORM) String platform,
                                                               @Query("quarter") int quarter,
                                                               @Query("start_year") int startYear,
                                                               @Query("tag_id") String tagId,
                                                               @Query(Constant.QUERY_TS) long ts,
                                                               @Query("update_period") int updatePeriod,
                                                               @Query("version") String version);

    @GET(Constant.BANGUMI_INDEX_COND)
    Call<BiliBiliResultResponse<BangumiIndexCond>> getBangumiIndexCond(@Query(Constant.QUERY_APP_KEY) String appKey,
                                                                       @Query(Constant.QUERY_BUILD) String build,
                                                                       @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                                       @Query(Constant.QUERY_PLATFORM) String platform,
                                                                       @Query(Constant.QUERY_TS) long ts,
                                                                       @Query(Constant.QUERY_TYPE) int type);

    @GET(Constant.APP_URL + Constant.PARTION_INFO)
    Call<BilibiliDataResponse<PartionHome>> getPartionInfo(@Query("rid") int rid,
                                                           @Query("channel") String channel,
                                                           @Query(Constant.QUERY_APP_KEY) String appKey,
                                                           @Query(Constant.QUERY_BUILD) String build,
                                                           @Query(Constant.QUERY_MOBI_APP) String mobiApp);

    @GET(Constant.APP_URL + Constant.PARTION_DYNAMIC)
    Call<BilibiliDataResponse<List<PartionVideo>>> getPartionDynamic(@Query("rid") int rid,
                                                                     @Query("pn") int pn,
                                                                     @Query("ps") int ps,
                                                                     @Query(Constant.QUERY_APP_KEY) String appKey,
                                                                     @Query(Constant.QUERY_BUILD) String build,
                                                                     @Query(Constant.QUERY_MOBI_APP) String mobiApp);

    @GET(Constant.APP_URL + Constant.PARTION_CHILD)
    Call<BilibiliDataResponse<PartionHome>> getPartionChild(@Query("rid") int rid,
                                                            @Query("channel") String channel,
                                                            @Query(Constant.QUERY_APP_KEY) String appKey,
                                                            @Query(Constant.QUERY_BUILD) String build,
                                                            @Query(Constant.QUERY_MOBI_APP) String mobiApp);

    @GET(Constant.APP_URL + Constant.PARTION_CHILD_LIST)
    Call<BilibiliDataResponse<List<PartionVideo>>> getPartionChildList(@Query("rid") int rid,
                                                                       @Query("pn") int pn,
                                                                       @Query("ps") int ps,
                                                                       @Query("order") String order,
                                                                       @Query(Constant.QUERY_APP_KEY) String appKey,
                                                                       @Query(Constant.QUERY_BUILD) String build,
                                                                       @Query(Constant.QUERY_MOBI_APP) String mobiApp);

    @GET(Constant.APP_URLS + Constant.VIDEO_DETAIL)
    Call<BilibiliDataResponse<VideoDetail>> getVideoDetail(@Query("aid") String aid,
                                                           @Query(Constant.QUERY_APP_KEY) String appkey,
                                                           @Query(Constant.QUERY_BUILD) String build,
//                                                           @Query("from") int from,
                                                           @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                           @Query("plat") int plat,
                                                           @Query(Constant.QUERY_PLATFORM) String platform,
                                                           @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.APP_URL + Constant.SEARCH)
    Call<BilibiliDataResponse<SearchResult>> getSearchResult(@Query("duration") int duration,
                                                             @Query("keyword") String keyword,
                                                             @Query("pn") int pn,
                                                             @Query("ps") int ps,
                                                             @Query(Constant.QUERY_APP_KEY) String appkey,
                                                             @Query(Constant.QUERY_BUILD) String build,
                                                             @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                             @Query(Constant.QUERY_PLATFORM) String platform);


    /**
     * @param keyword 关键字
     * @param pn      页码
     * @param ps      每页包含的数据个数
     * @param type    1 番剧 2 Up主
     * @return 结果
     */
    @GET(Constant.APP_URL + Constant.SEARCH_TYPE)
    Call<BilibiliDataResponse<BangumiSearchResult>> getBangumiSearchResult(@Query("keyword") String keyword,
                                                                           @Query("pn") int pn,
                                                                           @Query("ps") int ps,
                                                                           @Query("type") int type,
                                                                           @Query(Constant.QUERY_APP_KEY) String appkey,
                                                                           @Query(Constant.QUERY_BUILD) String build,
                                                                           @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                                           @Query(Constant.QUERY_PLATFORM) String platform);

    @GET(Constant.APP_URL + Constant.SEARCH_TYPE)
    Call<BilibiliDataResponse<UpSearchResult>> getUpSearchResult(@Query("keyword") String keyword,
                                                                 @Query("pn") int pn,
                                                                 @Query("ps") int ps,
                                                                 @Query("type") int type,
                                                                 @Query(Constant.QUERY_APP_KEY) String appkey,
                                                                 @Query(Constant.QUERY_BUILD) String build,
                                                                 @Query(Constant.QUERY_MOBI_APP) String mobiApp,
                                                                 @Query(Constant.QUERY_PLATFORM) String platform);

}

