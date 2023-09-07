package com.hdddekho.thirty.percent.customer.Utils;

public class MediaItem {

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;

    private int type;
    private String url;

    public MediaItem(int type, String url) {
        this.type = type;
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
