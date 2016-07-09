package com.lh.imbilibili;

import android.app.Application;
import android.os.Handler;

import com.lh.imbilibili.data.api.BilibiliApi;
import com.lh.imbilibili.data.api.BilibiliApiModule;

/**
 * Created by liuhui on 2016/7/5.
 */
public class IMBilibiliApplication extends Application {

    private static IMBilibiliApplication application;
    private IMBilibiliComponent bilibiliComponent;

    public static IMBilibiliApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        initComponent();
    }

    private void initComponent() {
        bilibiliComponent=DaggerIMBilibiliComponent.builder()
                .bilibiliApiModule(new BilibiliApiModule())
                .iMBilibiliModule(new IMBilibiliModule(this))
                .build();
    }

    public IMBilibiliComponent getBilibiliComponent() {
        return bilibiliComponent;
    }
}
