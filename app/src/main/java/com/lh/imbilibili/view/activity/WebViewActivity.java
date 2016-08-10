package com.lh.imbilibili.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lh.imbilibili.R;
import com.lh.imbilibili.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/7/30.
 */
public class WebViewActivity extends BaseActivity {

    public static final String EXTRA_DATA="url";

    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.nav_top_bar)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView webView;

    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        initStatusBar();
        initView();
    }

    private void initView() {
        toolbar.setTitle("加载中");
        url = getIntent().getStringExtra(EXTRA_DATA);
        webView.setWebChromeClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    public class MyWebViewClient extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setVisibility(newProgress == 100? View.GONE:View.VISIBLE);
            progressBar.setProgress(newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            toolbar.setTitle(title);
        }
    }
}