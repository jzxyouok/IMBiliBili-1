package com.lh.imbilibili.view.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;

/**
 * Created by home on 2016/7/30.
 */
public class GridRecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    private int itemHalfSpace;
    private int itemSpace;
    private boolean mIncludeEdge;

    public GridRecyclerViewItemDecoration(Context context, boolean includeEdge) {
        itemHalfSpace = context.getResources().getDimensionPixelSize(R.dimen.item_half_spacing);
        itemSpace = itemHalfSpace * 2;
        mIncludeEdge = includeEdge;
    }

    public GridRecyclerViewItemDecoration(int itemSpace, boolean includeEdge) {
        this.itemSpace = itemSpace;
        itemHalfSpace = itemSpace / 2;
        mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanCount = layoutManager.getSpanCount();
        int spanIndex = params.getSpanIndex();
        outRect.top = itemHalfSpace;
        outRect.bottom = itemHalfSpace;
        if (mIncludeEdge) {
//            outRect.left = itemSpace - itemSpace * spanIndex / spanCount;
            outRect.left = itemSpace * (spanCount - spanIndex) / spanCount;
            outRect.right = itemSpace * (spanIndex + 1) / spanCount;
        } else {
            outRect.left = itemSpace * spanIndex / spanCount;
            outRect.right = itemSpace * (spanCount - spanIndex - 1) / spanCount;
//            outRect.right = itemSpace - itemSpace * (spanIndex + 1) / spanCount;
        }
    }
}
