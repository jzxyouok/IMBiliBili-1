package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.search.BangumiSearchResult;
import com.lh.imbilibili.model.search.SearchResult;
import com.lh.imbilibili.model.search.UpSearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/8.
 */

public interface SearchService {

    @GET(Constant.APP_URL + Constant.SEARCH)
    Call<BilibiliDataResponse<SearchResult>> getSearchResult(@Query("duration") int duration,
                                                             @Query("keyword") String keyword,
                                                             @Query("pn") int pn,
                                                             @Query("ps") int ps);

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
                                                                           @Query("type") int type);

    @GET(Constant.APP_URL + Constant.SEARCH_TYPE)
    Call<BilibiliDataResponse<UpSearchResult>> getUpSearchResult(@Query("keyword") String keyword,
                                                                 @Query("pn") int pn,
                                                                 @Query("ps") int ps,
                                                                 @Query("type") int type);

}
