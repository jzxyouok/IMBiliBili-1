package com.lh.imbilibili.data;

import com.lh.imbilibili.utils.BiliBilliSignUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liuhui on 2016/7/8.
 */
public class BiliSignInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request finalRequest=null;
        Request oldlRequest =chain.request();
        if(oldlRequest.url().host().equals("bangumi.bilibili.com")) {
            HttpUrl.Builder urlBuilder = chain.request().url().newBuilder();
            String sign = BiliBilliSignUtils.getSign(oldlRequest.url().query());
            urlBuilder.addQueryParameter(Constant.QUERY_SIGN,sign);
            finalRequest=oldlRequest.newBuilder().url(urlBuilder.build()).build();
        }else {
            finalRequest=oldlRequest;
        }
        return chain.proceed(finalRequest);
    }
}