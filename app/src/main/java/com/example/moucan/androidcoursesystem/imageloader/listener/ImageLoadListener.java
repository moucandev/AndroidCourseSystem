package com.example.moucan.androidcoursesystem.imageloader.listener;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *  @class      LoadImageListener
 *  @author     xxZhu
 *  @describe   图片下载结果listener
 *
 */
public interface ImageLoadListener {
    void onLoadSuccess(@NonNull Bitmap bitmap);
    void onLoadFail(@Nullable Drawable errorDrawable);
}
