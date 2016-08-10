package com.lh.imbilibili.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/7/8.
 */
public class Bangumi implements Comparable<Bangumi>,Parcelable {
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
    @SerializedName("watching_count")
    private int watchingCount;
    @SerializedName("bangumi_id")
    private String bangumiId;
    @SerializedName("is_finish")
    private String isFinish;
    @SerializedName("is_new")
    private String isNew;
    private String follow;
    private String favourites;
    @SerializedName("pub_time")
    private long pubTime;
    private String version;

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

    public int getWatchingCount() {
        return watchingCount;
    }

    public void setWatchingCount(int watchingCount) {
        this.watchingCount = watchingCount;
    }

    public String getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(String bangumiId) {
        this.bangumiId = bangumiId;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getFavourites() {
        return favourites;
    }

    public void setFavourites(String favourites) {
        this.favourites = favourites;
    }

    public long getPubTime() {
        return pubTime;
    }

    public void setPubTime(long pubTime) {
        this.pubTime = pubTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(Bangumi another) {
        if(getWatchingCount()>another.getWatchingCount()) {
            return -1;
        }else {
            return 1;
        }
    }

    public Bangumi() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cover);
        dest.writeString(this.lastTime);
        dest.writeString(this.newestEpId);
        dest.writeString(this.newestEpIndex);
        dest.writeString(this.seasonId);
        dest.writeString(this.title);
        dest.writeString(this.totalCount);
        dest.writeInt(this.watchingCount);
        dest.writeString(this.bangumiId);
        dest.writeString(this.isFinish);
        dest.writeString(this.isNew);
        dest.writeString(this.follow);
        dest.writeString(this.favourites);
        dest.writeLong(this.pubTime);
        dest.writeString(this.version);
    }

    protected Bangumi(Parcel in) {
        this.cover = in.readString();
        this.lastTime = in.readString();
        this.newestEpId = in.readString();
        this.newestEpIndex = in.readString();
        this.seasonId = in.readString();
        this.title = in.readString();
        this.totalCount = in.readString();
        this.watchingCount = in.readInt();
        this.bangumiId = in.readString();
        this.isFinish = in.readString();
        this.isNew = in.readString();
        this.follow = in.readString();
        this.favourites = in.readString();
        this.pubTime = in.readLong();
        this.version = in.readString();
    }

    public static final Creator<Bangumi> CREATOR = new Creator<Bangumi>() {
        @Override
        public Bangumi createFromParcel(Parcel source) {
            return new Bangumi(source);
        }

        @Override
        public Bangumi[] newArray(int size) {
            return new Bangumi[size];
        }
    };
}
