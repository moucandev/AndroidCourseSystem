package com.example.moucan.androidcoursesystem.imageloader;

import android.text.TextUtils;

/**
 * @author xxZhu
 * @class UrlRequestOption
 * @describe 根据请求条件，构造新的url，不对外暴露
 */
class UrlRequestOption {
    private String url;
    private int sourceWidth;
    private int sourceHeight;
    private boolean needCutting;


    public UrlRequestOption() {
    }

    public UrlRequestOption(String url) {
        this.url = url;
    }

    public UrlRequestOption(String url, int sourceWidth, int sourceHeight, boolean needCutting) {
        this.url = url;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.needCutting = needCutting;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public void setSourceWidth(int sourceWidth) {
        this.sourceWidth = sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public void setSourceHeight(int sourceHeight) {
        this.sourceHeight = sourceHeight;
    }

    public boolean isNeedCutting() {
        return needCutting;
    }

    public void setNeedCutting(boolean needCutting) {
        this.needCutting = needCutting;
    }

    public String build() {
        return parseImageUrl(url, sourceWidth, sourceHeight, needCutting);
    }


    private String parseImageUrl(String url, int width, int height, boolean needCutting) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        StringBuilder newUrlBuilder = new StringBuilder(url);
        if (url.startsWith("//")) {
            newUrlBuilder.insert(0, "https:");
        }
        boolean hasWidthHeight = width > 0 && height > 0;
        if (hasWidthHeight || needCutting) {
            newUrlBuilder.append("?");
            if (hasWidthHeight) {
                newUrlBuilder.append("w=").append(width).append("&");
                newUrlBuilder.append("h=").append(height).append("&");
                newUrlBuilder.append("ratio=").append(width * 1.f / height);
            }
            if (needCutting) {
                if (hasWidthHeight) {
                    newUrlBuilder.append("&");
                }
                newUrlBuilder.append("rc=crop");
            }
        }
        return newUrlBuilder.toString();
    }


}
