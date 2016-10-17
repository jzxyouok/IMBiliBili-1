package com.lh.imbilibili.model.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liuhui on 2016/10/15.
 */

public class OfficialVerify implements Parcelable {
    private int type;
    private String desc;

    public OfficialVerify() {
    }

    protected OfficialVerify(Parcel in) {
        type = in.readInt();
        desc = in.readString();
    }

    public static final Creator<OfficialVerify> CREATOR = new Creator<OfficialVerify>() {
        @Override
        public OfficialVerify createFromParcel(Parcel in) {
            return new OfficialVerify(in);
        }

        @Override
        public OfficialVerify[] newArray(int size) {
            return new OfficialVerify[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(desc);
    }
}
