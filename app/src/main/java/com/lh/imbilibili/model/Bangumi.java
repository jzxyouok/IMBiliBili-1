package com.lh.imbilibili.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/7/8.
 */
public class Bangumi {
    private String cover;
    @SerializedName("last_time")
    private String lastTime;
    @SerializedName("newest_ep_id")
    private String newestEpId;
    @SerializedName("newest_ep_index")
    private String newestEpIndex;
    @SerializedName("season_id")
    private String seasonId;
    private String title;
    @SerializedName("total_count")
    private String totalCount;
    private String watchingCount;
    private String cursor;
    private String desc;
    private int id;
    @SerializedName("is_new")
    private int isNew=0;
    private String link;

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getNewestEpId() {
        return newestEpId;
    }

    public void setNewestEpId(String newestEpId) {
        this.newestEpId = newestEpId;
    }

    public String getNewestEpIndex() {
        return newestEpIndex;
    }

    public void setNewestEpIndex(String newestEpIndex) {
        this.newestEpIndex = newestEpIndex;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getWatchingCount() {
        return watchingCount;
    }

    public void setWatchingCount(String watchingCount) {
        this.watchingCount = watchingCount;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
