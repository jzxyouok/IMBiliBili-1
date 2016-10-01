package com.lh.imbilibili.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liuhui on 2016/7/5.
 */
public abstract class BaseFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentView(), container, false);
        initView(view);
        return view;
    }

    protected abstract void initView(View view);

    protected abstract int getContentView();

    public String getTitle() {
        return "";
    }
}
