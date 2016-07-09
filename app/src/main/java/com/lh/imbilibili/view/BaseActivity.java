package com.lh.imbilibili.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.data.api.BilibiliApi;
import com.lh.imbilibili.view.component.DaggerBaseActivityComponent;

import javax.inject.Inject;

/**
 * Created by liuhui on 2016/7/5.
 */
public class BaseActivity extends AppCompatActivity {
    @Inject
    protected BilibiliApi api;
    @Inject
    protected Handler handler;

    public BilibiliApi getApi() {
        return api;
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerBaseActivityComponent.builder()
                .iMBilibiliComponent(IMBilibiliApplication.getApplication().getBilibiliComponent())
                .build()
                .inject(this);
    }

}
