package com.lh.imbilibili.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
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
    private boolean canLoadMore = true;

    private LoadMoreScrollLinstener linstener;

    private onLoadMoreLinstener loadMoreLinstener;

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

    public void setOnLoadMoreLinstener(onLoadMoreLinstener loadMoreLinstener) {
        this.loadMoreLinstener = loadMoreLinstener;
    }

    public void setLoadingState(boolean loading) {
        isLoading = loading;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }


    public class LoadMoreScrollLinstener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LayoutManager layoutManager=recyclerView.getLayoutManager();
            View view = layoutManager.getChildAt(layoutManager.getChildCount()-1);
            if(newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !isLoading && loadMoreLinstener != null &&
                    canLoadMore && recyclerView.getChildViewHolder(view) instanceof LoadMoreRecyclerViewAdapter.LoadMoreViewHolder){
                isLoading = true;
                loadMoreLinstener.onLoadMore();
            }
        }
    }

    public static interface onLoadMoreLinstener {
        void onLoadMore();
    }

    public static abstract class LoadMoreRecyclerViewAdapter extends RecyclerView.Adapter {

        public static final int LOAD_MORE = -1;

        private boolean haveFoot = true;
        private LoadMoreViewHolder loadMoreViewHolder;

        public abstract int getRealItemCount();

        public abstract RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType);

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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_load_more_item, parent, false);
                loadMoreViewHolder=new LoadMoreViewHolder(view);
                return loadMoreViewHolder;
            }
            return onCreateHolder(parent, viewType);
        }

        @Override
        public int getItemCount() {
            if (haveFoot) {
                return getRealItemCount() + 1;
            } else {
                return getRealItemCount();
            }
        }

        public boolean isHaveFoot() {
            return haveFoot;
        }

        public void setLoadView(String text,boolean showProgress){
            if(loadMoreViewHolder!=null){
                loadMoreViewHolder.progressBar.setVisibility(showProgress?VISIBLE:GONE);
                loadMoreViewHolder.textView.setText(text);
            }
        }

        public void setHaveFoot(boolean haveFoot) {
            this.haveFoot = haveFoot;
        }

        public static class LoadMoreViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.progressBar)
            ProgressBar progressBar;
            @BindView(R.id.text)
            TextView textView;
            public LoadMoreViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
}
