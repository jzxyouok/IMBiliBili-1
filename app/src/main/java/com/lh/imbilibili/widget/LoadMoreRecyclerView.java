package com.lh.imbilibili.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lh.imbilibili.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/7/9.
 */
public class LoadMoreRecyclerView extends RecyclerView {

    private boolean isLoading = false;
    private boolean mEnableLoadMore = true;

    private LoadMoreAdapter mAdapter;

    private onLoadMoreLinstener mOnLoadMoreLinstener;
    private onLoadMoreViewClickListener mOnLoadMoreViewClickListener;

    private LoadMoreViewHolder mLoadMoreViewHolder;

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addOnScrollListener(new LoadMoreScrollLinstener());
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (!isInEditMode()) {
            mAdapter = (LoadMoreAdapter) adapter;
            View view = LayoutInflater.from(getContext()).inflate(R.layout.recyclerview_load_more_item, this, false);
            mLoadMoreViewHolder = new LoadMoreViewHolder(view);
            mLoadMoreViewHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnLoadMoreViewClickListener != null) {
                        mOnLoadMoreViewClickListener.onLoadMoreViewClick();
                    }
                }
            });
            setLoadMoreViewClickable(false);
            mAdapter.setmLoadMoreViewHolder(mLoadMoreViewHolder);
        }
    }

    public void setOnLoadMoreLinstener(onLoadMoreLinstener loadMoreLinstener) {
        mOnLoadMoreLinstener = loadMoreLinstener;
    }

    public void setOnLoadMoreViewClickListener(onLoadMoreViewClickListener clickListener) {
        setLoadMoreViewClickable(true);
        mOnLoadMoreViewClickListener = clickListener;
    }

    public void setLoadMoreViewClickable(boolean clickable) {
        mLoadMoreViewHolder.itemView.setClickable(clickable);
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setEnableLoadMore(boolean enableLoadMore) {
        mEnableLoadMore = enableLoadMore;
        setLoadMoreViewClickable(!enableLoadMore);
    }

    public void setLoadView(String text, boolean showProgress) {
        mLoadMoreViewHolder.progressBar.setVisibility(showProgress ? VISIBLE : GONE);
        mLoadMoreViewHolder.textView.setText(text);
    }

    public interface onLoadMoreLinstener {
        void onLoadMore();
    }

    public interface onLoadMoreViewClickListener {
        void onLoadMoreViewClick();
    }

    public static class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progressbar)
        ProgressBar progressBar;
        @BindView(R.id.text)
        TextView textView;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static abstract class LoadMoreAdapter extends RecyclerView.Adapter {

        public static final int LOAD_MORE = -1;

        private LoadMoreViewHolder mLoadMoreViewHolder;

        public abstract int getRealItemCount();

        public abstract RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType);

        public abstract void onBindHolder(ViewHolder holder, int position);

        public abstract int getItemType(int position);

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() - 1 == position) {
                return LOAD_MORE;
            } else {
                return getItemType(position);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == LOAD_MORE) {
                return mLoadMoreViewHolder;
            }
            return onCreateHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (getItemViewType(position) != LOAD_MORE) {
                onBindHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            return getRealItemCount() + 1;
        }

        private void setmLoadMoreViewHolder(LoadMoreViewHolder holder) {
            mLoadMoreViewHolder = holder;
        }
    }

    public class LoadMoreScrollLinstener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            View view = layoutManager.getChildAt(layoutManager.getChildCount() - 1);
            if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !isLoading && mOnLoadMoreLinstener != null &&
                    mEnableLoadMore && recyclerView.getChildViewHolder(view) instanceof LoadMoreViewHolder &&
                    mAdapter != null && mAdapter.getRealItemCount() > 0) {
                isLoading = true;
                mOnLoadMoreLinstener.onLoadMore();
            }
        }
    }
}
