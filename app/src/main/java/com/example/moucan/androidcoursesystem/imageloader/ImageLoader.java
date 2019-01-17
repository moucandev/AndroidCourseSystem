package com.example.moucan.androidcoursesystem.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.pwrd.dls.marble.common.imageloader.glide.GlideImageLoaderStrategy;
import com.pwrd.dls.marble.common.imageloader.listener.ImageLoadListener;
import com.pwrd.dls.marble.common.imageloader.listener.ImageLoadProgressListener;
import com.pwrd.dls.marble.common.imageloader.listener.ImageSaveListener;


/**
 * @author xxZhu
 * @class PreImageLoader
 * @describe 图片加载器[策略模式]
 */
public class ImageLoader {


    static {
        mStrategy = new GlideImageLoaderStrategy();
    }
    private static IImageLoaderStrategy mStrategy;
    public void setLoadImgStrategy(IImageLoaderStrategy strategy) {
        mStrategy = strategy;
    }



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
        if (mStrategy != null) {
            mStrategy.loadImage(context, url, isSvg, isCircle, isCenterCrop,placeHolder, imageView, viewWidth, viewHeight, loadListener, loadProgressListener);
        }
    }


    /**
     * 判断图片是否在缓存里
     *
     * @param url
     *
     * @return
     */
    public static boolean checkImageExist(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return mStrategy.checkImageExist(url);
    }

    /**
     * 清除图片磁盘缓存
     */
    public static void clearImageDiskCache(final Context context) {
        mStrategy.clearImageDiskCache(context);
    }

    /**
     * 清除图片内存缓存
     */
    public static void clearImageMemoryCache(Context context) {
        mStrategy.clearImageMemoryCache(context);
    }

    /**
     * 根据不同的内存状态，来响应不同的内存释放策略
     *
     * @param context
     * @param level
     */
    public static void trimMemory(Context context, int level) {
        mStrategy.trimMemory(context, level);
    }

    /**
     * 清除图片所有缓存
     */
    public static void clearImageAllCache(Context context) {
        clearImageDiskCache(context.getApplicationContext());
        clearImageMemoryCache(context.getApplicationContext());
    }

    /**
     * 获取缓存大小
     *
     * @return CacheSize
     */
    public static long getCacheSize(Context context) {
        return mStrategy.getCacheSize(context);
    }

    public static void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener) {
        mStrategy.saveImage(context, url, savePath, saveFileName, listener);
    }
    public static void saveImage(Context context, Bitmap bitmap, String savePath, String saveFileName, ImageSaveListener listener){
        mStrategy.saveImage(context, bitmap, savePath, saveFileName, listener);
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

    public static final void resumeRequest(Context context){
        mStrategy.resumeRequest(context);
    }
}
