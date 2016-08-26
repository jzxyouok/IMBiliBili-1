package com.lh.imbilibili.view.adapter.feedbackfragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.Feedback;
import com.lh.imbilibili.model.FeedbackData;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.widget.FeedbackView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/8/2.
 */
public class FeedbackAdapter extends LoadMoreRecyclerView.LoadMoreAdapter {

    public static final int FEEDBACK_ITEM = 1;
    public static final int HOT_FEEDBACK_ITEM = 2;
    public static final int HOT_FEEDBACK_FOOTER = 3;

    private FeedbackData feedbackData;

    private OnFeedbackItemClickListener onFeedbackItemClickListener;

    public FeedbackAdapter(FeedbackData feedbackData) {
        this.feedbackData = feedbackData;
    }

    public void setOnFeedbackItemClickListener(OnFeedbackItemClickListener OnFeedbackItemClickListener) {
        this.onFeedbackItemClickListener = OnFeedbackItemClickListener;
    }

    public void setFeedbackData(FeedbackData feedbackData) {
        this.feedbackData = feedbackData;
    }

    @Override
    public int getRealItemCount() {
        if (feedbackData == null || feedbackData.getReplies() == null) {
            return 0;
        } else {
            if (feedbackData.getHots() == null) {
                return feedbackData.getReplies().size();
            } else {
                return feedbackData.getReplies().size() + feedbackData.getHots().size();
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = null;
        if (viewType == FEEDBACK_ITEM || viewType == HOT_FEEDBACK_ITEM) {
            View view = inflater.inflate(R.layout.feedback_list_item_with_reply, parent, false);
            holder = new FeedbackHolder(view);
        } else if (viewType == HOT_FEEDBACK_FOOTER) {
            View view = inflater.inflate(R.layout.bangumi_season_comment_footer, parent, false);
            holder = new HotFeedbackFooterHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemType(position) == HOT_FEEDBACK_ITEM) {
            FeedbackHolder feedbackHolder = (FeedbackHolder) holder;
            Feedback feedback = feedbackData.getHots().get(position);
            feedbackHolder.feedbackView.setData(feedback);
        } else if (getItemType(position) == FEEDBACK_ITEM) {
            FeedbackHolder feedbackHolder = (FeedbackHolder) holder;
            Feedback feedback;
            if (feedbackData.getHots() != null && feedbackData.getHots().size() > 0) {
                feedback = feedbackData.getReplies().get(position - 4);
            } else {
                feedback = feedbackData.getReplies().get(position);
            }
            feedbackHolder.feedbackView.setData(feedback);
            if (feedback.getReplies() != null && feedback.getReplies().size() > 0) {
                feedbackHolder.replyGroup.setVisibility(View.VISIBLE);
                feedbackHolder.replyGroup.removeAllViews();
                addReply(feedbackHolder, feedback);
            } else {
                feedbackHolder.replyGroup.setVisibility(View.GONE);
            }
        } else {
            HotFeedbackFooterHolder hotFeedbackFooterHolder = (HotFeedbackFooterHolder) holder;
            hotFeedbackFooterHolder.tvTitle.setText("更多热门评论>>");
        }
    }

    private void addReply(FeedbackHolder feedbackHolder, Feedback feedback) {
        LayoutInflater inflater = LayoutInflater.from(feedbackHolder.itemView.getContext());
        for (int i = 0; i < feedback.getReplies().size(); i++) {
            Feedback reply = feedback.getReplies().get(i);
            FeedbackHolder.ReplyHolder replyHolder = feedbackHolder.generateReplyHolder(inflater);
            if (i == feedback.getReplies().size() - 1) {
                replyHolder.divider.setVisibility(View.GONE);
            }
            replyHolder.tvMessage.setText(reply.getContent().getMessage());
            replyHolder.tvName.setText(reply.getMember().getUname());
            replyHolder.tvPubTime.setText(StringUtils.formateDate(reply.getCtime()));
            feedbackHolder.replyGroup.addView(replyHolder.itemView);
        }
    }

    @Override
    public int getItemType(int position) {
        if (feedbackData.getHots() != null && feedbackData.getHots().size() != 0) {
            if (position < 3) {
                return HOT_FEEDBACK_ITEM;
            } else if (position == 3) {
                return HOT_FEEDBACK_FOOTER;
            } else {
                return FEEDBACK_ITEM;
            }
        } else {
            return FEEDBACK_ITEM;
        }
    }

    private void clickItem(int type, int position) {
        if (onFeedbackItemClickListener != null) {
            onFeedbackItemClickListener.onFeedbackItemClick(type, position);
        }
    }

    public interface OnFeedbackItemClickListener {
        void onFeedbackItemClick(int type, int position);
    }

    public class FeedbackHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.feedback)
        FeedbackView feedbackView;
        @BindView(R.id.reply_group)
        LinearLayout replyGroup;

        public FeedbackHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem(getItemType(getAdapterPosition()), getAdapterPosition());
                }
            });
        }

        public ReplyHolder generateReplyHolder(LayoutInflater inflater) {
            View view = inflater.inflate(R.layout.feedback_list_item_reply, replyGroup, false);
            return new ReplyHolder(view);
        }

        public class ReplyHolder {
            @BindView(R.id.name)
            TextView tvName;
            @BindView(R.id.pub_time)
            TextView tvPubTime;
            @BindView(R.id.message)
            TextView tvMessage;
            @BindView(R.id.divider)
            View divider;

            View itemView;

            public ReplyHolder(View itemView) {
                this.itemView = itemView;
                ButterKnife.bind(this, itemView);
            }
        }
    }

    public class HotFeedbackFooterHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView tvTitle;

        public HotFeedbackFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem(HOT_FEEDBACK_ITEM, getAdapterPosition());
                }
            });
        }
    }
}
