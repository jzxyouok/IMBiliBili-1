package com.lh.imbilibili.view.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/7/8.
 */
public class BangumiRecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
    private int itemHalfSpace;
    private int itemSpace;

    public BangumiRecyclerViewItemDecoration(Context context) {
        itemHalfSpace = context.getResources().getDimensionPixelSize(R.dimen.item_half_spacing);
        itemSpace = itemHalfSpace * 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
        int position = holder.getLayoutPosition();
        GridLayoutManager.LayoutParams params= (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex=params.getSpanIndex();
        if (adapter.getItemViewType(position) == BangumiRecyclerViewAdapter.BANNER) {
            outRect.top = 0;
            outRect.bottom = itemHalfSpace;
            outRect.left = 0;
            outRect.right = 0;
        } else if (adapter.getItemViewType(position) == BangumiRecyclerViewAdapter.NAV ||
                adapter.getItemViewType(position) == BangumiRecyclerViewAdapter.LATEST_UPDATE_HEAD ||
                adapter.getItemViewType(position) == BangumiRecyclerViewAdapter.END_BANGUMI_HEAD ||
                adapter.getItemViewType(position) == BangumiRecyclerViewAdapter.END_BANGUMI_GROUP ||
                adapter.getItemViewType(position) == BangumiRecyclerViewAdapter.BANGUMI_RECOMMEND_HEAD ||
                adapter.getItemViewType(position) == BangumiRecyclerViewAdapter.BANGUMI_RECOMMEND_ITEM) {
            outRect.top = itemHalfSpace;
            outRect.bottom = itemHalfSpace;
            outRect.left = itemSpace;
            outRect.right = itemSpace;
        } else if(adapter.getItemViewType(position) == BangumiRecyclerViewAdapter.LATEST_UPDATE_GRID){
            outRect.top=itemHalfSpace;
            outRect.bottom=itemHalfSpace;
            if(spanIndex==0){
                outRect.left=itemSpace;
                outRect.right=itemHalfSpace;
            }else {
                outRect.left=itemHalfSpace;
                outRect.right=itemSpace;
            }
        }
    }
}
