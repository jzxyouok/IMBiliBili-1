package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.BiliSignInterceptor;
import com.lh.imbilibili.data.Constant;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liuhui on 2016/7/5.
 */
@Module
public class BilibiliApiModule {
    @Singleton
    @Provides
    public Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor logi = new HttpLoggingInterceptor();
        logi.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().writeTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .addInterceptor(new BiliSignInterceptor())
                .addInterceptor(logi)
                .build();
    }

    @Singleton
    @Provides
    public BilibiliApi provideBilibiliApi(Retrofit retrofit) {
        return retrofit.create(BilibiliApi.class);
    }
}
