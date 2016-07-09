package com.lh.imbilibili.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.Bangumi;
import com.lh.imbilibili.model.IndexPage;
import com.lh.imbilibili.widget.BannerView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by liuhui on 2016/7/6.
 */
public class BangumiRecyclerViewAdapter extends LoadMoreRecyclerView.LoadMoreRecyclerViewAdapter {

    public static final int BANNER = 0;
    public static final int NAV = 1;
    public static final int LATEST_UPDATE_HEAD = 2;
    public static final int LATEST_UPDATE_GRID = 3;
    public static final int END_BANGUMI_HEAD = 4;
    public static final int END_BANGUMI_GROUP = 5;
    public static final int BANGUMI_RECOMMEND_HEAD = 6;
    public static final int BANGUMI_RECOMMEND_ITEM = 7;

    private IndexPage indexPage;
    private List<Bangumi> bangumis;

    private Context context;

    public BangumiRecyclerViewAdapter(IndexPage indexPage, List<Bangumi> bangumis, Context context,RecyclerView recyclerView) {
        this.indexPage = indexPage;
        this.bangumis = bangumis;
        this.context = context;
    }

    @Override
    public int getItemType(int position) {
        if (position == 0) {
            return BANNER;
        } else if (position == 1) {
            return NAV;
        } else if (position == 2) {
            return LATEST_UPDATE_HEAD;
        } else if (position >= 3 && position <= 8) {
            return LATEST_UPDATE_GRID;
        } else if (position == 9) {
            return END_BANGUMI_HEAD;
        } else if (position == 10) {
            return END_BANGUMI_GROUP;
        } else if (position == 11) {
            return BANGUMI_RECOMMEND_HEAD;
        } else {
            return BANGUMI_RECOMMEND_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == BANNER) {
            View bannerView = inflater.inflate(R.layout.banner_item, parent, false);
            BannerAdapter adapter = new BannerAdapter(indexPage.getBanners());
            holder = new BannerHolder(bannerView, adapter);
        } else if (viewType == NAV) {
            View navView = inflater.inflate(R.layout.bangumi_nav_item, parent, false);
            holder = new NavHolder(navView);
        } else if (viewType == LATEST_UPDATE_HEAD) {
            View headView = inflater.inflate(R.layout.bangumi_latest_update_head_item, parent, false);
            holder = new HeadUpdateCountHolder(headView);
        } else if (viewType == LATEST_UPDATE_GRID) {
            View gridView = inflater.inflate(R.layout.bangumi_latest_grid_item, parent, false);
            holder = new LatestUpdateGridHolder(gridView);
        } else if (viewType == END_BANGUMI_HEAD) {
            View headView = inflater.inflate(R.layout.bangumi_end_head_item, parent, false);
            holder = new HeadHolder(headView);
        } else if (viewType == END_BANGUMI_GROUP) {
            View container = inflater.inflate(R.layout.bangumi_end_group_item, parent, false);
            holder = new EndBangumiGroupHolder(container);
        } else if (viewType == BANGUMI_RECOMMEND_HEAD) {
            View headView = inflater.inflate(R.layout.bangumi_recommend_head_item, parent, false);
            holder = new HeadHolder(headView);
        } else if (viewType == BANGUMI_RECOMMEND_ITEM) {
            View itemView = inflater.inflate(R.layout.bangumi_recommend_item, parent, false);
            holder = new BangumiRecommendHolder(itemView);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == LATEST_UPDATE_GRID) {
            LatestUpdateGridHolder gridHolder = (LatestUpdateGridHolder) holder;
            List<Bangumi> bangumis = indexPage.getLatestUpdate().getList();
            Bangumi bangumi = bangumis.get(position - 3);
            Glide.with(context).load(bangumi.getCover()).into(gridHolder.ivCover);
            gridHolder.tvTitle.setText(bangumi.getTitle());
            gridHolder.tv1.setText(String.format(Locale.getDefault(), "第%s话", bangumi.getNewestEpIndex()));
            gridHolder.tv2.setText(bangumi.getLastTime());
            gridHolder.tv3.setText(String.format(Locale.getDefault(), "%s人在看", bangumi.getWatchingCount()));
        } else if (getItemViewType(position) == END_BANGUMI_GROUP) {
            EndBangumiGroupHolder groupHolder = (EndBangumiGroupHolder) holder;
            List<Bangumi> bangumis = indexPage.getEnds();
            for (int i = 0; i < 3; i++) {
                Bangumi bangumi = bangumis.get(i);
                EndBangumiGroupHolder.BangumiItem bangumiItem = groupHolder.bangumiItems.get(i);
                Glide.with(context).load(bangumi.getCover()).into(bangumiItem.ivCover);
                bangumiItem.tvTitle.setText(bangumi.getTitle());
                bangumiItem.tv1.setText(String.format(Locale.getDefault(), "%s话全", bangumi.getTotalCount()));
            }
        } else if (getItemViewType(position) == BANGUMI_RECOMMEND_ITEM) {
            BangumiRecommendHolder recommendHolder = (BangumiRecommendHolder) holder;
            Bangumi bangumi = bangumis.get(position - 12);
            Glide.with(context).load(bangumi.getCover()).into(recommendHolder.ivCover);
            recommendHolder.tvTitle.setText(bangumi.getTitle());
            recommendHolder.tv1.setText(bangumi.getDesc());
            recommendHolder.ivBadge.setVisibility(bangumi.getIsNew() == 1 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getRealItemCount() {
        if (indexPage == null) {
            return 0;
        } else if (bangumis == null) {
            return 11;
        } else {
            return 11 + bangumis.size();
        }
    }

    public void setIndexPage(IndexPage indexPage) {
        this.indexPage = indexPage;
    }

    public void setBangumis(List<Bangumi> bangumis) {
        this.bangumis = bangumis;
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof BannerHolder) {
            BannerHolder bannerHolder = (BannerHolder) holder;
            bannerHolder.bannerView.startLoop(6000);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof BannerHolder) {
            BannerHolder bannerHolder = (BannerHolder) holder;
            bannerHolder.bannerView.stopLoop();
        }
    }

    public static class BannerHolder extends RecyclerView.ViewHolder {

        BannerView bannerView;

        public BannerHolder(View itemView, BannerAdapter adapter) {
            super(itemView);
            bannerView = (BannerView) itemView;
            bannerView.setAdaper(adapter);
            bannerView.startLoop(6000);
            adapter.notifyDataSetChanged();
        }
    }

    public static class NavHolder extends RecyclerView.ViewHolder {
        public NavHolder(View itemView) {
            super(itemView);
        }
    }

    public static class HeadUpdateCountHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_update_count)
        TextView tvUpdateCount;

        public HeadUpdateCountHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class HeadHolder extends RecyclerView.ViewHolder {

        public HeadHolder(View itemView) {
            super(itemView);
        }
    }

    public static class LatestUpdateGridHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cover)
        ImageView ivCover;
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.text1)
        TextView tv1;
        @BindView(R.id.text2)
        TextView tv2;
        @BindView(R.id.text3)
        TextView tv3;

        public LatestUpdateGridHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class EndBangumiGroupHolder extends RecyclerView.ViewHolder {

        List<BangumiItem> bangumiItems;

        @BindView(R.id.bangumi)
        View bangumi;
        @BindView(R.id.bangumi_1)
        View bangumi1;
        @BindView(R.id.bangumi_2)
        View bangumi2;

        public EndBangumiGroupHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            bangumiItems = new ArrayList<>();
            bangumiItems.add(new BangumiItem(bangumi, 0));
            bangumiItems.add(new BangumiItem(bangumi1, 1));
            bangumiItems.add(new BangumiItem(bangumi2, 2));
        }

        public static class BangumiItem {
            View itemView;

            @BindView(R.id.cover)
            ImageView ivCover;
            @BindView(R.id.title)
            TextView tvTitle;
            @BindView(R.id.text1)
            TextView tv1;

            public BangumiItem(View itemView, int index) {
                this.itemView = itemView;
//                int widthPixels = itemView.getResources().getDisplayMetrics().widthPixels;
//                int itemSpace = itemView.getResources().getDimensionPixelSize(R.dimen.item_spacing);
                ButterKnife.bind(this, itemView);
//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemView.getLayoutParams();
//                if (index == 0) {
//                    params.rightMargin = itemSpace / 2;
//                } else if (index == 1) {
//                    params.leftMargin = itemSpace / 2;
//                    params.rightMargin = itemSpace / 2;
//                } else {
//                    params.leftMargin = itemSpace / 2;
//                }
//                int ivWidth = (widthPixels - 3 * itemSpace) / 3;
//                ViewGroup.LayoutParams ivParams = ivCover.getLayoutParams();
//                ivParams.height = (int) (ivWidth * 320.0 / 240);
//                ivCover.setLayoutParams(ivParams);
//                itemView.setLayoutParams(params);
            }
        }
    }

    public static class BangumiRecommendHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView ivCover;
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.badge)
        ImageView ivBadge;
        @BindView(R.id.text1)
        TextView tv1;

        public BangumiRecommendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
