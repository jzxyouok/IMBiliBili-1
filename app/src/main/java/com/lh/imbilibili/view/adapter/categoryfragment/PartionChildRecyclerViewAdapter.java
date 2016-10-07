package com.lh.imbilibili.view.adapter.categoryfragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.PartionHome;
import com.lh.imbilibili.model.PartionVideo;
import com.lh.imbilibili.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/1.
 */

public class PartionChildRecyclerViewAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HOT_HEAD = 1;
    private static final int TYPE_HOT_ITEM = 2;
    private static final int TYPE_NEW_HEAD = 3;
    private static final int TYPE_NEW_ITEM = 4;

    private PartionHome mPartionHomeData;
    private List<PartionVideo> mNewVideos;

    private OnVideoItemClickListener mOnVideoItemClickListener;

    private Context mContext;

    public PartionChildRecyclerViewAdapter(Context context) {
        mContext = context;
        mNewVideos = new ArrayList<>();
    }

    public void setOnVideoItemClickListener(OnVideoItemClickListener listener) {
        mOnVideoItemClickListener = listener;
    }

    public void addNewVideos(List<PartionVideo> newVideos) {
        if (mNewVideos == null) {
            mNewVideos = newVideos;
        } else {
            mNewVideos.addAll(newVideos);
        }
    }

    public void setPartionHomeData(PartionHome partionHomeData) {
        mPartionHomeData = partionHomeData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HOT_HEAD:
            case TYPE_NEW_HEAD:
                viewHolder = new HeadHolder(new TextView(parent.getContext()));
                break;
            case TYPE_HOT_ITEM:
            case TYPE_NEW_ITEM:
                viewHolder = new VideoHolder(inflater.inflate(R.layout.video_list_item, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HOT_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.mHeadName.setText("最热视频");
        } else if (type == TYPE_HOT_ITEM) {
            VideoHolder videoHolder = (VideoHolder) holder;
            int partPosition = position - 1;
            PartionVideo video = mPartionHomeData.getRecommend().get(partPosition);
            Glide.with(mContext).load(video.getCover()).into(videoHolder.mIvCover);
            videoHolder.mTvTitle.setText(video.getTitle());
            videoHolder.mTvAuthor.setText(video.getName());
            videoHolder.mTvInfoViews.setText(StringUtils.formateNumber(video.getPlay()));
            videoHolder.mTvInfoDanmakus.setText(StringUtils.formateNumber(video.getDanmaku()));
            videoHolder.mTvPayBadge.setVisibility(View.GONE);
            videoHolder.mAid = video.getParam();
        } else if (type == TYPE_NEW_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.mHeadName.setText("最新视频");
        } else if (type == TYPE_NEW_ITEM) {
            VideoHolder videoHolder = (VideoHolder) holder;
            int partPosition;
            if (mPartionHomeData != null && mPartionHomeData.getRecommend() != null && mPartionHomeData.getRecommend().size() != 0) {
                partPosition = position - 6;
            } else {
                partPosition = position;
            }
            PartionVideo video = mNewVideos.get(partPosition);
            Glide.with(mContext).load(video.getCover()).into(videoHolder.mIvCover);
            videoHolder.mTvTitle.setText(video.getTitle());
            videoHolder.mTvAuthor.setText(video.getName());
            videoHolder.mTvInfoViews.setText(StringUtils.formateNumber(video.getPlay()));
            videoHolder.mTvInfoDanmakus.setText(StringUtils.formateNumber(video.getDanmaku()));
            videoHolder.mTvPayBadge.setVisibility(View.GONE);
            videoHolder.mAid = video.getParam();
        }
    }

    @Override
    public int getItemCount() {
        if (mPartionHomeData == null) {
            return 0;
        } else if (mPartionHomeData.getRecommend().size() == 0) {
            return mNewVideos.size() + 1;
        } else {
            return 6 + mNewVideos.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mPartionHomeData != null && mPartionHomeData.getRecommend().size() != 0) {
            switch (position) {
                case 0:
                    return TYPE_HOT_HEAD;
                case 1:
                case 2:
                case 3:
                case 4:
                    return TYPE_HOT_ITEM;
                case 5:
                    return TYPE_NEW_HEAD;
                default:
                    return TYPE_NEW_ITEM;
            }
        } else {
            switch (position) {
                case 0:
                    return TYPE_NEW_HEAD;
                default:
                    return TYPE_NEW_ITEM;
            }
        }
    }

    private class HeadHolder extends RecyclerView.ViewHolder {
        private TextView mHeadName;

        HeadHolder(View itemView) {
            super(itemView);
            mHeadName = (TextView) itemView;
        }
    }

    @SuppressWarnings("WeakerAccess")
    class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.pay_badge)
        TextView mTvPayBadge;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.author)
        TextView mTvAuthor;
        @BindView(R.id.info_views)
        TextView mTvInfoViews;
        @BindView(R.id.info_danmakus)
        TextView mTvInfoDanmakus;
        @BindView(R.id.text2)
        TextView mTvSecond;

        private String mAid;

        VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            int tintColor = ContextCompat.getColor(mContext, R.color.gray_dark);
            Drawable drawableCompat = DrawableCompat.wrap(mTvInfoViews.getCompoundDrawables()[0]);
            DrawableCompat.setTint(drawableCompat, tintColor);
            drawableCompat = DrawableCompat.wrap(mTvInfoDanmakus.getCompoundDrawables()[0]);
            DrawableCompat.setTint(drawableCompat, tintColor);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnVideoItemClickListener != null) {
                mOnVideoItemClickListener.onVideoClick(mAid);
            }
        }
    }

    public interface OnVideoItemClickListener {
        void onVideoClick(String aid);
    }
}
