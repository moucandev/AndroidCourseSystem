package com.example.moucan.androidcoursesystem.imageloader.glide;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.caverock.androidsvg.SVG;
import com.pwrd.dls.marble.common.net.upload.ProgressManager;
import com.pwrd.dls.marble.common.svg.SvgDecoder;
import com.pwrd.dls.marble.common.svg.SvgDrawableTranscoder;

import java.io.InputStream;

import androidx.annotation.NonNull;

/**
 * @author xxZhu
 * @class MyGlideModule
 * @describe Glide配置
 */
@GlideModule
public class MyGlideModule extends AppGlideModule {

    int diskSize = 1024 * 1024 * 100;

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        // Apply options to the builder here.
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取系统分配给应用的总内存大小
        int memoryCacheSize = maxMemory / 8;//设置图片内存缓存占用八分之一
        //设置内存缓存大小
        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(memoryCacheSize));
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context));
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
        //暂时使用默认缓存
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "glideCache", diskSize)); //sd卡中
    }


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(ProgressManager.getInstance().getOkHttpClient()));
        registry.register(SVG.class, PictureDrawable.class, new SvgDrawableTranscoder())
                .append(InputStream.class, SVG.class, new SvgDecoder());
    }


    @Override
    public boolean isManifestParsingEnabled() {
        return false;

    }
}
