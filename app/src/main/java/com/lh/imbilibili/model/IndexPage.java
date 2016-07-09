package com.lh.imbilibili.model;

import java.util.List;

/**
 * Created by liuhui on 2016/7/8.
 */
public class IndexPage {
    private List<Banner> banners;
    private List<Bangumi> ends;
    private LatestUpdate latestUpdate;

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public List<Bangumi> getEnds() {
        return ends;
    }

    public void setEnds(List<Bangumi> ends) {
        this.ends = ends;
    }

    public LatestUpdate getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(LatestUpdate latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    public static class LatestUpdate{
        private List<Bangumi> list;
        private String updateCount;

        public List<Bangumi> getList() {
            return list;
        }

        public void setList(List<Bangumi> list) {
            this.list = list;
        }

        public String getUpdateCount() {
            return updateCount;
        }

        public void setUpdateCount(String updateCount) {
            this.updateCount = updateCount;
        }
    }
}
