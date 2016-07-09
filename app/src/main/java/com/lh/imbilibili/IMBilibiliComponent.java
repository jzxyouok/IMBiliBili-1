package com.lh.imbilibili;

import android.content.Context;
import android.os.Handler;

import com.lh.imbilibili.data.api.BilibiliApi;
import com.lh.imbilibili.data.api.BilibiliApiModule;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by liuhui on 2016/7/5.
 */
@Singleton
@Component(modules = {BilibiliApiModule.class,IMBilibiliModule.class})
public interface IMBilibiliComponent {

    BilibiliApi getBiliApi();

    OkHttpClient getOkhttpClient();

    Retrofit getRetrofit();

    Context getContext();

    Handler getHandler();
}
