package com.example.moucan.androidcoursesystem.imageloader.listener;

/**
 *  @class      ImageLoadProgressListener
 *  @author     xxZhu
 *  @describe   图片下载进度的回调
 *
 */
public interface ImageLoadProgressListener extends  ImageLoadListener {
    void onProgress(int percent);
}
