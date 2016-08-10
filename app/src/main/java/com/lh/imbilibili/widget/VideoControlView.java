package com.lh.imbilibili.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lh.ijkplayer.widget.IjkVideoView;
import com.lh.imbilibili.R;
import com.lh.imbilibili.utils.StringUtils;

import java.lang.ref.WeakReference;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by lh on 2016/8/6.
 * 视频播放界面
 */
public class VideoControlView extends FrameLayout implements SeekBar.OnSeekBarChangeListener, IMediaPlayer.OnBufferingUpdateListener, View.OnClickListener {

    public static final int MSG_HIDE_UI = 1;
    public static final int MSG_HIDE_VOLUME_BAR = 2;
    public static final int MSG_HIDE_BRIGHTNESS_BAR = 3;
    public static final int MSG_HIDE_MEDIA_CONTROL = 4;
    public static final int MSG_UPDATE_MEDIA_CONTROL_VIEW = 5;
    public static final int MSG_HIDE_GESTUREINFO_VIEW = 6;

    private static final long TIME_OUT = 2000;
    private static final long MEDIA_CONTROL_TIME_OUT = 3000;

    private IjkVideoView mIjkVideoView;

    private LinearLayout mVolumeBar;
    private ProgressBar mPbVolumeLevel;
    private LinearLayout mBrightnessBar;
    private ProgressBar mPbBrightnessLevel;

    private View mMediaControlView;
    private SeekBar mSeekBar;
    private ImageView mIvPlayPause;
    private TextView mTvCurrentTime;
    private TextView mTvTotalTime;
    private TextView mTvTitle;
    private TextView mTvQualitySelect;
    private TextView mTvVideoInfo;
    private ImageView mIvBack;

    private LinearLayout mGestureInfoGroup;
    private TextView mGestureInfoText;
    private TextView mGestureInfoText1;

//    private LinearLayout mBufferingGroup;

    private AudioManager mAudioManager;
    private ControlHandler mHandler;

//    private AndroidMediaController mMediaController;

    private GestureDetector mGestureDetector;
    private GestureType mGestureType = GestureType.None;

    private PopupWindow mQualityPopuWindow;

    private boolean mShowing;

    private int mVolume;
    private int mMaxVolume;
    private int mCurrentPosition;
    private int mCurrentBufferPercentage;

    private float mTotalVolumeOffset;
    private float mTotalBrightnessOffset;
    private float mTotalPlayPositionOffset;

    private int mCurrentQuality;

    private OnQualitySelectListener mOnQualitySelectListener;

    private enum GestureType {
        None,
        Volume,
        Brightness,
        FastBackwardOrForward,
        SingleTapConfirmed,
        DoubleTap
    }

    public VideoControlView(Context context) {
        this(context, null, 0);
    }

    public VideoControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mShowing = false;
        mCurrentPosition = -1;
        mCurrentBufferPercentage = 0;
        setClickable(true);
        LayoutInflater inflater = LayoutInflater.from(context);
//        initPrePlayView(context);
//        initVideoView(context);
//        initBufferingView(context, inflater);
        initMediaControlView(context, inflater);
        initMediaLevelView(context, inflater);
        initGestureInfoView(context, inflater);
        initGesture(context);
    }

//    private void initPrePlayView(Context context) {
//        mPrePlayMessage = new TextView(context);
//        mPrePlayProgressBar = new ProgressBar(context);
//        mPrePlayMessage.setTextColor(ContextCompat.getColor(context, R.color.gray_light));
//        LayoutParams preMsgParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        LayoutParams prePbParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        prePbParams.gravity = Gravity.CENTER;
//        preMsgParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
//        addView(mPrePlayMessage, preMsgParams);
//        addView(mPrePlayProgressBar, prePbParams);
//    }
//
//    private void initVideoView(Context context) {
//        mIjkVideoView = new IjkVideoView(context);
//        mIjkVideoView.setVisibility(GONE);
//        mIjkVideoView.setClickable(false);
//        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        addView(mIjkVideoView, params);
//        mIjkVideoView.setOnPreparedListener(this);
//        mIjkVideoView.setOnInfoListener(this);
//    }
//
//    private void initBufferingView(Context context, LayoutInflater inflater) {
//        View view = inflater.inflate(R.layout.player_buffering_view, this, false);
//        view.setClickable(false);
//        mBufferingGroup = (LinearLayout) view.findViewById(R.id.buffering_grop);
//        addView(mBufferingGroup);
//    }

    private void initMediaControlView(Context context, LayoutInflater inflater) {
        mMediaControlView = inflater.inflate(R.layout.player_control_view, this, false);
        mSeekBar = (SeekBar) mMediaControlView.findViewById(R.id.seekbar);
        mIvPlayPause = (ImageView) mMediaControlView.findViewById(R.id.play_pause_toggle);
        mTvCurrentTime = (TextView) mMediaControlView.findViewById(R.id.current_time);
        mTvTotalTime = (TextView) mMediaControlView.findViewById(R.id.total_time);
        mTvTitle = (TextView) mMediaControlView.findViewById(R.id.title);
        mTvQualitySelect = (TextView) mMediaControlView.findViewById(R.id.quality_select);
        mIvBack = (ImageView) mMediaControlView.findViewById(R.id.back);
        mTvVideoInfo = (TextView) mMediaControlView.findViewById(R.id.video_info);
        ViewGroup popuView = (ViewGroup) inflater.inflate(R.layout.popu_quality_select_view,null);
        for(int i =0;i<popuView.getChildCount();i++){
            popuView.getChildAt(i).setOnClickListener(this);
        }
        mQualityPopuWindow =new PopupWindow(popuView,mTvQualitySelect.getLayoutParams().width,ViewGroup.LayoutParams.WRAP_CONTENT);
        mQualityPopuWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(),R.color.black_60_alpha)));
        mQualityPopuWindow.setOutsideTouchable(true);
        mSeekBar.setOnSeekBarChangeListener(this);
        addView(mMediaControlView);
        mTvVideoInfo.setOnClickListener(this);
        mIvPlayPause.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mTvQualitySelect.setOnClickListener(this);
    }

    private void initMediaLevelView(Context context, LayoutInflater inflater) {
        mHandler = new ControlHandler(this);
        if (!isInEditMode()) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        View mediaLevelView = inflater.inflate(R.layout.player_media_level_bar, this, false);
        mVolumeBar = (LinearLayout) mediaLevelView.findViewById(R.id.volume_bar);
        mPbVolumeLevel = (ProgressBar) mediaLevelView.findViewById(R.id.volume_level);
        mBrightnessBar = (LinearLayout) mediaLevelView.findViewById(R.id.brightness_bar);
        mPbBrightnessLevel = (ProgressBar) mediaLevelView.findViewById(R.id.brightness_level);
        mPbVolumeLevel.setMax(mMaxVolume);
        addView(mediaLevelView);
    }

    private void initGestureInfoView(Context context, LayoutInflater inflater) {
        mGestureInfoGroup = (LinearLayout) inflater.inflate(R.layout.player_gesture_info, this, false);
        mGestureInfoText = (TextView) mGestureInfoGroup.findViewById(R.id.text);
        mGestureInfoText1 = (TextView) mGestureInfoGroup.findViewById(R.id.text1);
        addView(mGestureInfoGroup);
    }

    private void initGesture(Context context) {
        mGestureDetector = new GestureDetector(context, new MyVideoViewGestureListener());
    }

    public void setVideoView(IjkVideoView videoView) {
        mIjkVideoView = videoView;
        mIjkVideoView.setOnBufferingUpdateListener(this);
    }

    public void setVideoTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setOnQualitySelectListener(OnQualitySelectListener l){
        mOnQualitySelectListener = l;
    }

    public void setCurrentVideoQuality(int quality){
        mCurrentQuality = quality;
        mTvQualitySelect.setText(qualityCodeForString(mCurrentQuality));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (mGestureType == GestureType.Volume) {
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_VOLUME_BAR, TIME_OUT);
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_GESTUREINFO_VIEW, TIME_OUT);
            } else if (mGestureType == GestureType.Brightness) {
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_BRIGHTNESS_BAR, TIME_OUT);
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_GESTUREINFO_VIEW, TIME_OUT);
            } else if (mGestureType == GestureType.FastBackwardOrForward) {
                mIjkVideoView.seekTo(mCurrentPosition);
                mCurrentPosition = -1;
                mBeforeScrollPosition = -1;
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_GESTUREINFO_VIEW, TIME_OUT);
            } else if (mGestureType == GestureType.SingleTapConfirmed) {
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_MEDIA_CONTROL, MEDIA_CONTROL_TIME_OUT);
            }
            mTotalPlayPositionOffset = 0;
            mTotalVolumeOffset = 0;
            mTotalBrightnessOffset = 0;
            mGestureType = GestureType.None;
        }
        return true;
    }

    private void showMediaControlView() {
        mShowing = true;
        mHandler.sendEmptyMessage(MSG_UPDATE_MEDIA_CONTROL_VIEW);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_MEDIA_CONTROL,MEDIA_CONTROL_TIME_OUT);
        mMediaControlView.setVisibility(VISIBLE);
        mTvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    }

    private void hideMediaControlView() {
        mShowing = false;
        mHandler.removeMessages(MSG_UPDATE_MEDIA_CONTROL_VIEW);
        mMediaControlView.setVisibility(GONE);
        mTvTitle.setEllipsize(TextUtils.TruncateAt.END);
    }

    private void hideGestureInfoView() {
        mGestureInfoGroup.setVisibility(GONE);
    }

    public IjkVideoView getIjkVideoView() {
        return mIjkVideoView;
    }

    public boolean isShowing() {
        return mShowing;
    }

    private void updateMediaControlView() {
        mIvPlayPause.setImageLevel(mIjkVideoView.isPlaying() ? 1 : 0);
        int total = mIjkVideoView.getDuration();
        int currentPosition = mIjkVideoView.getCurrentPosition();
        float percent = 100f * currentPosition / total;
        mTvCurrentTime.setText(stringForTime(currentPosition));
        mTvTotalTime.setText(stringForTime(total));
        mSeekBar.setProgress((int) percent);
        mSeekBar.setSecondaryProgress(mCurrentBufferPercentage);
    }

    private void playOrPause() {
        if (mIjkVideoView.isPlaying()) {
            mIvPlayPause.setImageLevel(0);
            mIjkVideoView.pause();
        } else {
            mIvPlayPause.setImageLevel(1);
            mIjkVideoView.start();
        }
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if (hours > 0) {
            return StringUtils.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return StringUtils.format("%02d:%02d", minutes, seconds);
        }
    }

    private String qualityCodeForString(int quality){
        if(quality == 1){
            return "流畅";
        }else if(quality == 2){
            return "高清";
        }else {
            return "超清";
        }
    }

    private void setFastBackwardForwardGestureInfo(int afterTime, int preTime, int totalTime) {
        mHandler.removeMessages(MSG_HIDE_GESTUREINFO_VIEW);
        mGestureInfoGroup.setVisibility(VISIBLE);
        mGestureInfoText1.setVisibility(VISIBLE);
        mGestureInfoText.setText("");
        mGestureInfoText.append(stringForTime(afterTime));
        mGestureInfoText.append("/");
        mGestureInfoText.append(stringForTime(totalTime));
        mGestureInfoText1.setText(StringUtils.format("%+d秒", (afterTime - preTime) / 1000));
    }

    private void setVolumeGestureInfo(int percent) {
        mHandler.removeMessages(MSG_HIDE_GESTUREINFO_VIEW);
        mGestureInfoGroup.setVisibility(VISIBLE);
        mGestureInfoText1.setVisibility(GONE);
        if (percent <= 0) {
            mGestureInfoText.setText("静音");
        } else {
            mGestureInfoText.setText(StringUtils.format("音量:%d%%", percent));
        }
    }

    private void setBrightnessGestureInfo(int percent) {
        mHandler.removeMessages(MSG_HIDE_GESTUREINFO_VIEW);
        mGestureInfoGroup.setVisibility(VISIBLE);
        mGestureInfoText1.setVisibility(GONE);
        if (percent <= 0) {
            mGestureInfoText.setText("最低亮度");
        } else {
            mGestureInfoText.setText(StringUtils.format("亮度:%d%%", percent));
        }
    }

    private void setVolume(float percent) {
        mHandler.removeMessages(MSG_HIDE_VOLUME_BAR);
        mVolumeBar.setVisibility(View.VISIBLE);
        float offsetVolume = percent * mMaxVolume * 1.2f;
        mTotalVolumeOffset += offsetVolume;
        if (mTotalVolumeOffset > 1) {
            mVolume += mTotalVolumeOffset;
            mTotalVolumeOffset -= Math.floor(mTotalVolumeOffset);
        } else if (mTotalVolumeOffset < -1) {
            mVolume += Math.ceil(mTotalVolumeOffset);
            mTotalVolumeOffset -= Math.ceil(mTotalVolumeOffset);
        }
        if (mVolume > mMaxVolume) {
            mVolume = mMaxVolume;
        } else if (mVolume < 0) {
            mVolume = 0;
        }
        setVolumeGestureInfo(100 * mVolume / mMaxVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        mPbVolumeLevel.setProgress(mVolume);
    }

    private void setBrightness(float percent) {
        System.out.println(percent);
        mHandler.removeMessages(MSG_HIDE_BRIGHTNESS_BAR);
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            mHandler.removeMessages(MSG_HIDE_UI);
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            float brightness = lp.screenBrightness;
            mTotalBrightnessOffset += percent * 1.2;
            float gate = 1.0f / 15;
            if (mTotalBrightnessOffset > gate) {
                brightness += mTotalBrightnessOffset;
                mTotalBrightnessOffset = 0;
            } else if (mTotalBrightnessOffset < -gate) {
                brightness += mTotalBrightnessOffset;
                mTotalBrightnessOffset = 0;
            }
            if (brightness > 1) {
                brightness = 1;
            } else if (brightness < 0) {
                brightness = 0;
            }
            lp.screenBrightness = brightness;
            mBrightnessBar.setVisibility(View.VISIBLE);
            setBrightnessGestureInfo((int) (brightness * 100));
            mPbBrightnessLevel.setProgress((int) (brightness * 100));
            activity.getWindow().setAttributes(lp);
        }
    }

    private void hideVolumeBar() {
        mVolumeBar.setVisibility(GONE);
    }

    private void hideBrightnessBar() {
        mBrightnessBar.setVisibility(GONE);
    }

    private boolean mScrollingSeekBar = false;
    private int mBeforeScrollPosition;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mIjkVideoView != null && mScrollingSeekBar) {
            float percent = progress / 100f;
            int totalTime = mIjkVideoView.getDuration();
            setFastBackwardForwardGestureInfo((int) (percent * totalTime), mBeforeScrollPosition, totalTime);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mScrollingSeekBar = true;
        mHandler.removeMessages(MSG_HIDE_GESTUREINFO_VIEW);
        mHandler.removeMessages(MSG_UPDATE_MEDIA_CONTROL_VIEW);
        mHandler.removeMessages(MSG_HIDE_MEDIA_CONTROL);
        mBeforeScrollPosition = mIjkVideoView.getCurrentPosition();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mScrollingSeekBar = false;
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_GESTUREINFO_VIEW, TIME_OUT);
        if (mShowing) {
            mHandler.sendEmptyMessage(MSG_UPDATE_MEDIA_CONTROL_VIEW);
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_MEDIA_CONTROL, MEDIA_CONTROL_TIME_OUT);
        }
        if (mIjkVideoView != null) {
            float percent = seekBar.getProgress() / 100f;
            int pos = (int) (percent * mIjkVideoView.getDuration());
            mIjkVideoView.seekTo(pos);
        }
    }


    private static class ControlHandler extends Handler {
        private WeakReference<VideoControlView> mVideoControlView;

        private ControlHandler(VideoControlView view) {
            mVideoControlView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mVideoControlView.get() != null) {
                switch (msg.what) {
                    case MSG_HIDE_UI:
                        break;
                    case MSG_HIDE_VOLUME_BAR:
                        mVideoControlView.get().hideVolumeBar();
                        break;
                    case MSG_HIDE_BRIGHTNESS_BAR:
                        mVideoControlView.get().hideBrightnessBar();
                        break;
                    case MSG_HIDE_MEDIA_CONTROL:
                        if(!mVideoControlView.get().mQualityPopuWindow.isShowing()){
                            mVideoControlView.get().hideMediaControlView();
                        }
                        break;
                    case MSG_UPDATE_MEDIA_CONTROL_VIEW:
                        mVideoControlView.get().updateMediaControlView();
                        if (mVideoControlView.get().getIjkVideoView().isPlaying() && mVideoControlView.get().isShowing()) {
                            sendEmptyMessageDelayed(MSG_UPDATE_MEDIA_CONTROL_VIEW, 1000);
                        }
                        break;
                    case MSG_HIDE_GESTUREINFO_VIEW:
                        mVideoControlView.get().hideGestureInfoView();
                        break;
                }
            }
        }
    }

    private class MyVideoViewGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mGestureType == GestureType.None) {
                mGestureType = GestureType.SingleTapConfirmed;
                mHandler.removeMessages(MSG_HIDE_MEDIA_CONTROL);
                if (mShowing) {
                    hideMediaControlView();
                } else {
                    showMediaControlView();
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mGestureType == GestureType.None) {
                mGestureType = GestureType.DoubleTap;
                playOrPause();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mGestureType == GestureType.None) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    mGestureType = GestureType.FastBackwardOrForward;
                } else {
                    if (e1.getX() > getWidth() * 2.0 / 3) {//右边
                        mGestureType = GestureType.Volume;
                    } else if (e1.getX() < getWidth() / 3.0) {//左边
                        mGestureType = GestureType.Brightness;
                    } else {
                        return false;
                    }
                }
            }
            if (mGestureType == GestureType.Volume) {
                setVolume(distanceY / getHeight());
            } else if (mGestureType == GestureType.Brightness) {
                setBrightness(distanceY / getHeight());
            } else if (mGestureType == GestureType.FastBackwardOrForward) {
                if (mCurrentPosition < 0) {
                    mCurrentPosition = mIjkVideoView.getCurrentPosition();
                    mBeforeScrollPosition = mCurrentPosition;
                }
                float percent = distanceX / getWidth();
                mTotalPlayPositionOffset += percent * 90;
                if (mTotalPlayPositionOffset > 1) {
                    mCurrentPosition -= 1000 * Math.floor(mTotalPlayPositionOffset);
                    mTotalPlayPositionOffset -= Math.floor(mTotalPlayPositionOffset);
                } else if (mTotalPlayPositionOffset < -1) {
                    mCurrentPosition -= 1000 * Math.ceil(mTotalPlayPositionOffset);
                    mTotalPlayPositionOffset -= Math.ceil(mTotalPlayPositionOffset);
                }
                if (mCurrentPosition > mIjkVideoView.getDuration()) {
                    mCurrentPosition = mIjkVideoView.getDuration();
                } else if (mCurrentPosition < 0) {
                    mCurrentPosition = 0;
                }
                setFastBackwardForwardGestureInfo(mCurrentPosition, mBeforeScrollPosition, mIjkVideoView.getDuration());
            }
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.smooth_quality: case R.id.high_quality:case R.id.super_high_quality:
                mQualityPopuWindow.dismiss();
                mHandler.sendEmptyMessage(MSG_HIDE_MEDIA_CONTROL);
                if(mOnQualitySelectListener!=null){
                    mOnQualitySelectListener.onQualitySelect(Integer.parseInt((String) v.getTag()));
                }
                break;
            case R.id.play_pause_toggle:
                playOrPause();
                break;
            case R.id.back:
                ((Activity)getContext()).finish();
                break;
            case R.id.video_info:
                if(mIjkVideoView!=null){
                    mIjkVideoView.showMediaInfo();
                }
                break;
            case R.id.quality_select:
                System.out.println("show");
                mQualityPopuWindow.showAsDropDown(v);
                break;
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        mCurrentBufferPercentage = percent;
    }

    public interface OnQualitySelectListener{
        void onQualitySelect(int quality);
    }
}
