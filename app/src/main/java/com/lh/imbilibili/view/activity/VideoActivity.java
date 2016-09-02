package com.lh.imbilibili.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.ijkplayer.widget.IjkVideoView;
import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BangumiDetail;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.SourceData;
import com.lh.imbilibili.model.VideoPlayData;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.StorageUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.widget.VideoControlView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by home on 2016/8/3.
 */
public class VideoActivity extends BaseActivity implements IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, VideoControlView.OnQualitySelectListener, IMediaPlayer.OnPreparedListener {

    @BindView(R.id.pre_play_msg)
    TextView mPrePlayMsg;
    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_buffering)
    TextView mTvBuffering;
    @BindView(R.id.videoview)
    IjkVideoView mIjkVideoView;
    @BindView(R.id.video_control_view)
    VideoControlView mVideoControlView;
    @BindView(R.id.danmaku_view)
    IDanmakuView mDanmakuView;

    private BangumiDetail.Episode mEpisode;
    private String mBangumiTitle;
    private List<SourceData> mSourceDatas;
    private VideoPlayData mVideoPlayData;

    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;

    private Call<BiliBiliResultResponse<List<SourceData>>> sourceInfoCall;
    private okhttp3.Call danmakuCall;
    private Call<VideoPlayData> playInfoCall;

    private UpdatePrePlayMsgReceiver mPrePlayMsgReceiver;

    private boolean mPlayerChanged = false;
    private boolean mResumePlay = false;
    private boolean mIsFirstLoadVideo = true;
    private int mPrePlayerPosition;
    private long firstBackPressTime = -1;

    public static void startVideoActivity(Context context, BangumiDetail.Episode episode, String title) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("data", episode);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        mPrePlayMsgReceiver = new UpdatePrePlayMsgReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mPrePlayMsgReceiver, new IntentFilter(IjkVideoView.ACTION));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        ButterKnife.bind(this);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(getString(com.example.ijkplayer.R.string.pref_key_using_media_codec), true).commit();
        mEpisode = getIntent().getParcelableExtra("data");
        mBangumiTitle = getIntent().getStringExtra("title");
        initIjkPlayer();
        appendVideoMsg(null, StringUtils.format("正在载入(id=%s)", mEpisode.getEpisodeId()), false);
        appendVideoMsg(null, "正在解析视频信息...", true);
        appendVideoMsg(null, "正在解析播放地址...", true);
        mVideoControlView.setVideoView(mIjkVideoView);
        initDanmakuView();
        loadSourceInfo();
    }

    private void initIjkPlayer() {
        mVideoControlView.setVideoTitle(StringUtils.format("%s-%s", mEpisode.getIndex(), mBangumiTitle));
        mIjkVideoView.setKeepScreenOn(true);
        mIjkVideoView.setOnPreparedListener(this);
        mIjkVideoView.setOnInfoListener(this);
        mIjkVideoView.setOnErrorListener(this);
        mVideoControlView.setOnQualitySelectListener(this);
    }

    private void loadSourceInfo() {
        sourceInfoCall = IMBilibiliApplication.getApplication().getApi().getSource(Constant.APPKEY, Constant.BUILD, mEpisode.getEpisodeId(), Constant.MOBI_APP, Constant.PLATFORM, System.currentTimeMillis());
        sourceInfoCall.enqueue(new Callback<BiliBiliResultResponse<List<SourceData>>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<List<SourceData>>> call, Response<BiliBiliResultResponse<List<SourceData>>> response) {
                if (response.body().getCode() == 0) {
                    mSourceDatas = response.body().getResult();
                    SourceData sourceData = mSourceDatas.get(0);
                    appendVideoMsg("正在解析视频信息...", StringUtils.format("成功(av_id=%s cid=%s)", sourceData.getAvId(), sourceData.getCid()), false);
                    downloadDanmaku(sourceData.getCid());
                    loadPlayInfo(sourceData.getAvId(), sourceData.getCid(), 3);
                    mVideoControlView.setCurrentVideoQuality(3);
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResultResponse<List<SourceData>>> call, Throwable t) {
                appendVideoMsg("正在解析视频信息...", "失败", false);
            }
        });
    }

    private void loadPlayInfo(String aid, String cid, final int quality) {
        playInfoCall = IMBilibiliApplication.getApplication().getApi().getPlayData(Constant.PLATFORM, Constant.BUILD, Constant.PLATFORM, aid, 0, Integer.valueOf(mEpisode.getIndex()) - 1, 0, cid, quality, "json", Constant.PLAYER_APPKEY);
//        playInfoCall = IMBilibiliApplication.getApplication().getApi().getPlayData(Constant.PLAYER_APPKEY, cid, "json", quality, "mp4");
        playInfoCall.enqueue(new Callback<VideoPlayData>() {
            @Override
            public void onResponse(Call<VideoPlayData> call, Response<VideoPlayData> response) {
                if (response.body() != null && response.body().getDurl() != null) {
                    appendVideoMsg("正在解析播放地址...", "成功", false);
                    mVideoPlayData = response.body();
                    if (mIsFirstLoadVideo) {
                        mIsFirstLoadVideo = false;
                        mIjkVideoView.setVideoPath(concatVideo(mVideoPlayData.getDurl()));
                    } else {
                        mIjkVideoView.changeVideoPath(concatVideo(mVideoPlayData.getDurl()));
                    }
                    mVideoControlView.setCurrentVideoQuality(quality);
                    mIjkVideoView.start();
                } else {
                    appendVideoMsg("正在解析播放地址...", "失败", false);
                }
            }

            @Override
            public void onFailure(Call<VideoPlayData> call, Throwable t) {
                appendVideoMsg("正在解析播放地址...", "失败", false);
            }
        });
    }

    public String concatVideo(List<VideoPlayData.Durl> durls) {
        String videoPlayPath = null;
        if (durls.size() > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ffconcat version 1.0\n");
            for (int i = 0; i < durls.size(); i++) {
                stringBuilder.append("file ");
                stringBuilder.append(durls.get(i).getUrl());
                stringBuilder.append("\n");
                stringBuilder.append("duration ");
                stringBuilder.append(durls.get(i).getLength() / 1000);
                stringBuilder.append("\n");
            }
            File file = StorageUtils.getAppCache(getApplicationContext(), "video.cancat");
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
                fileWriter.write(stringBuilder.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            videoPlayPath = file.getAbsolutePath();
        } else if (durls.size() == 1) {
            videoPlayPath = durls.get(0).getUrl();
        }
        return videoPlayPath;
    }

    public void appendVideoMsg(@Nullable String orginMsg, String appendMsg, boolean newLine) {
        String str;
        String preStr = mPrePlayMsg.getText().toString();
        if (orginMsg != null && preStr.contains(orginMsg)) {
            str = preStr.replace(orginMsg, orginMsg + appendMsg);
            mPrePlayMsg.setText(str);
        } else {
            if (newLine) {
                mPrePlayMsg.append("\n");
            }
            mPrePlayMsg.append(appendMsg);
        }
    }

    private void initDanmakuView() {
        mDanmakuContext = DanmakuContext.create();
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
    }

    private void downloadDanmaku(String cid) {
        OkHttpClient okHttpClient = IMBilibiliApplication.getApplication().getBilibiliComponent().getOkhttpClient();
        Request request = new Request.Builder().url(Constant.COMMENT_URL + "/" + cid + ".xml").build();
        danmakuCall = okHttpClient.newCall(request);
        danmakuCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                File danmakuFile = StorageUtils.getAppCache(getApplicationContext(), "danmaku.xml");
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(danmakuFile);
                    fileWriter.write(response.body().string());
                    fileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fileWriter != null) {
                            fileWriter.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    preparDanmaku(new FileInputStream(danmakuFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }
        });
    }

    private void preparDanmaku(InputStream stream) {
        if (mDanmakuView != null) {
            mParser = createParser(stream);
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
//                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                }
            });
            mDanmakuView.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {
                @Override
                public void onDanmakuClick(BaseDanmaku latest) {
                    Log.d("DFM", "onDanmakuClick text:" + latest.text);
                }

                @Override
                public void onDanmakuClick(IDanmakus danmakus) {
                    Log.d("DFM", "onDanmakuClick danmakus size:" + danmakus.size());
                }
            });
            mDanmakuView.prepare(mParser, mDanmakuContext);
            mDanmakuView.showFPS(true);
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }


    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mResumePlay) {
            mIjkVideoView.start();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIjkVideoView.isPlaying()) {
            mResumePlay = true;
            mIjkVideoView.pause();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPrePlayMsgReceiver);
        CallUtils.cancelCall(sourceInfoCall, playInfoCall);
        if (danmakuCall != null && !danmakuCall.isCanceled()) {
            danmakuCall.cancel();
        }
        mIjkVideoView.stopPlayback();
        mIjkVideoView.release(true);
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                mProgressBar.setVisibility(View.GONE);
                mTvBuffering.setVisibility(View.GONE);
                mPrePlayMsg.setVisibility(View.GONE);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                mTvBuffering.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                mProgressBar.setVisibility(View.GONE);
                mTvBuffering.setVisibility(View.GONE);
                mPrePlayMsg.setVisibility(View.GONE);
                break;
        }
        return true;
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        if (!mPlayerChanged) {
            mIjkVideoView.togglePlayer();
            mIjkVideoView.start();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (firstBackPressTime < -1) {
            ToastUtils.showToast(this, "再按一次退出", Toast.LENGTH_SHORT);
            firstBackPressTime = System.currentTimeMillis();
        } else {
            long secondBackPressTime = System.currentTimeMillis();
            if (secondBackPressTime - firstBackPressTime < 2000) {
                super.onBackPressed();
                if (mDanmakuView != null) {
                    // dont forget release!
                    mDanmakuView.release();
                    mDanmakuView = null;
                }
            } else {
                ToastUtils.showToast(this, "再按一次退出", Toast.LENGTH_SHORT);
                firstBackPressTime = secondBackPressTime;
            }
        }
    }

    @Override
    public void onQualitySelect(int quality) {
        mPrePlayerPosition = mIjkVideoView.getCurrentPosition();
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        loadPlayInfo(mSourceDatas.get(0).getAvId(), mSourceDatas.get(0).getCid(), quality);
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        if (!mIsFirstLoadVideo) {
            mIjkVideoView.seekTo(mPrePlayerPosition);
        }
    }

    public class UpdatePrePlayMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            appendVideoMsg(null, intent.getStringExtra("msg"), true);
        }
    }
}
