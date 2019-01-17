package com.example.moucan.androidcoursesystem.imageloader;

import android.content.Context;

import android.graphics.drawable.Drawable;

import android.widget.ImageView;


import com.example.moucan.androidcoursesystem.imageloader.listener.ImageLoadListener;
import com.example.moucan.androidcoursesystem.imageloader.listener.ImageLoadProgressListener;



/**
 * @class PreImageLoader
 * @describe 图片加载器[策略模式]
 */
public class ImageLoader {





    public static ImageLoadRequestBuilder with(Context context) {
        return new ImageLoadRequestBuilder(context);
    }

    static void loadImage(Context context,
                          String url, boolean isSvg,
                          boolean isCircle,
                          boolean isCenterCrop,
                          Drawable placeHolder,
                          ImageView imageView, int viewWidth, int viewHeight,
                          ImageLoadListener loadListener,
                          ImageLoadProgressListener loadProgressListener) {
    }





    /**
     * 根据条件获得新的url
     *
     * @param url
     * @param width
     * @param height
     * @param needCutting
     *
     * @return
     */
    public static String parseImageUrl(String url, int width, int height, boolean needCutting) {
        return new UrlRequestOption(url, width, height, needCutting).build();
    }


}
