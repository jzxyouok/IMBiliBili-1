package com.lh.danmakulibrary;

import android.support.annotation.NonNull;

/**
 * Created by liuhui on 2016/9/23.
 */

public class Danmaku implements Comparable<Danmaku> {
    private long time;//s
    private int type;
    private int textSize;
    private int textColor;
    private String content;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Danmaku parse(String str) {
        Danmaku danmaku = new Danmaku();
        String[] strs = str.split(",");
        danmaku.setTime((long) (Float.parseFloat(strs[0]) * 1000L));
        danmaku.setType(Integer.parseInt(strs[1]));
        danmaku.setTextSize(Integer.parseInt(strs[2]));
        danmaku.setTextColor(Integer.parseInt(strs[3]) | 0xFF000000);
        return danmaku;
    }

    @Override
    public int compareTo(@NonNull Danmaku another) {
        return time < another.getTime() ? -1 : 1;
    }
}
