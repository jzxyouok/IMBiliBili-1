package com.lh.imbilibili;

import android.app.Application;
import android.os.Handler;

import com.lh.imbilibili.data.api.BilibiliApi;
import com.lh.imbilibili.data.api.BilibiliApiModule;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by liuhui on 2016/7/5.
 */
public class IMBilibiliApplication extends Application {

    private static IMBilibiliApplication application;
    private IMBilibiliComponent bilibiliComponent;

    private BilibiliApi mApi;
    private Handler mHandler;

    public static IMBilibiliApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        LeakCanary.install(this);
        initComponent();
    }

    private void initComponent() {
        bilibiliComponent = DaggerIMBilibiliComponent.builder()
                .bilibiliApiModule(new BilibiliApiModule())
                .iMBilibiliModule(new IMBilibiliModule(this))
                .build();
        mApi = bilibiliComponent.getBiliApi();
        mHandler = bilibiliComponent.getHandler();
    }

    public BilibiliApi getApi() {
        return mApi;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public IMBilibiliComponent getBilibiliComponent() {
        return bilibiliComponent;
    }
}
