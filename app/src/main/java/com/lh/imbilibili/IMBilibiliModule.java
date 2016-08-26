package com.lh.imbilibili;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuhui on 2016/7/5.
 */
@Module
public class IMBilibiliModule {
    private Application application;

    public IMBilibiliModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return application;
    }

    @Singleton
    @Provides
    Handler providerHandler() {
        return new Handler(Looper.getMainLooper());
    }
}
