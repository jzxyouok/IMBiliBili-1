package com.lh.danmakulibrary;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by liuhui on 2016/9/23.
 */

public class BiliBiliDanmakuParser {

    public ArrayList<Danmaku> parse(InputStream stream) throws Exception {
        ArrayList<Danmaku> danmakus = null;
        Danmaku danmaku = null;
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(stream, "utf-8");
        int eventType = pullParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    danmakus = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    if (pullParser.getName().equals("d")) {
                        String attrs = pullParser.getAttributeValue(0);
                        danmaku = Danmaku.parse(attrs);
                        if (pullParser.next() == XmlPullParser.TEXT) {
                            danmaku.setContent(pullParser.getText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (pullParser.getName().equals("d")) {
                        danmakus.add(danmaku);
                    }
                    break;
            }
            eventType = pullParser.next();
        }
        Collections.sort(danmakus);
        return danmakus;
    }
}
