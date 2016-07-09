package com.lh.imbilibili.view.component;

import com.lh.imbilibili.IMBilibiliComponent;
import com.lh.imbilibili.scope.ActivityScope;
import com.lh.imbilibili.view.BaseActivity;

import dagger.Component;

/**
 * Created by liuhui on 2016/7/5.
 */
@ActivityScope
@Component(dependencies = IMBilibiliComponent.class)
public interface BaseActivityComponent {

    void inject(BaseActivity activity);

}
