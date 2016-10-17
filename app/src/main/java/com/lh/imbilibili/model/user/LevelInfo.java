package com.lh.imbilibili.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/10/15.
 */

public class LevelInfo implements Parcelable {
    @SerializedName("current_level")
    private int currentLevel;
    @SerializedName("current_min")
    private int currentMin;
    @SerializedName("current_exp")
    private int currentExp;
    @SerializedName("next_exp")
    private String nextExp;

    public LevelInfo() {
    }

    protected LevelInfo(Parcel in) {
        currentLevel = in.readInt();
        currentMin = in.readInt();
        currentExp = in.readInt();
        nextExp = in.readString();
    }

    public static final Creator<LevelInfo> CREATOR = new Creator<LevelInfo>() {
        @Override
        public LevelInfo createFromParcel(Parcel in) {
            return new LevelInfo(in);
        }

        @Override
        public LevelInfo[] newArray(int size) {
            return new LevelInfo[size];
        }
    };

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getCurrentMin() {
        return currentMin;
    }

    public void setCurrentMin(int currentMin) {
        this.currentMin = currentMin;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(int currentExp) {
        this.currentExp = currentExp;
    }

    public String getNextExp() {
        return nextExp;
    }

    public void setNextExp(String nextExp) {
        this.nextExp = nextExp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(currentLevel);
        dest.writeInt(currentMin);
        dest.writeInt(currentExp);
        dest.writeString(nextExp);
    }
}