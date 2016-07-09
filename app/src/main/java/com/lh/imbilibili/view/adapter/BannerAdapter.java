package com.lh.imbilibili.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.model.Banner;
import com.lh.imbilibili.widget.BannerView;
import com.lh.imbilibili.widget.ScalableImageView;

import java.util.List;

/**
 * Created by liuhui on 2016/7/8.
 */
public class BannerAdapter extends BannerView.Adaper {

    private List<Banner> banners;

    public BannerAdapter(List<Banner> banners) {
        this.banners = banners;
    }

    @Override
    public int getBannerCount() {
        if(banners==null){
            return 0;
        }else{
            return banners.size();
        }
    }

    @Override
    public Object getItemView(ViewGroup container, int position) {
        ScalableImageView imageView=new ScalableImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setWidthRatio(960);
        imageView.setHeightRatio(300);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        Glide.with(container.getContext()).load(banners.get(position).getImg()).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }
}
