package com.lh.imbilibili.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lh.imbilibili.R;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.fragment.MainFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit();
    }
}
