package com.example.moucan.androidcoursesystem.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.pwrd.dls.marble.common.imageloader.listener.ImageLoadListener;
import com.pwrd.dls.marble.common.imageloader.listener.ImageLoadProgressListener;
import com.pwrd.dls.marble.common.imageloader.listener.ImageSaveListener;


/**
 * @author xxZhu
 * @class BaseImageLoaderStrategy
 * @describe
 */
public interface IImageLoaderStrategy {

    void loadImage(Context context,
                   String url, boolean isSvg,
                   boolean isCircle,
                   boolean isCenterCrop,
                   Drawable placeHolder,
                   ImageView imageView, int viewWidth, int viewHeight,
                   ImageLoadListener loadListener,
                   ImageLoadProgressListener loadProgressListener);


    boolean checkImageExist(String url);


    //清除硬盘缓存
    void clearImageDiskCache(final Context context);

    //清除内存缓存
    void clearImageMemoryCache(Context context);

    //根据不同的内存状态，来响应不同的内存释放策略
    void trimMemory(Context context, int level);

    //获取缓存大小
    long getCacheSize(Context context);

    void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener);
    void saveImage(Context context, Bitmap bitmap, String savePath, String saveFileName, ImageSaveListener listener);


    //恢复请求
    void resumeRequest(Context context);

}
