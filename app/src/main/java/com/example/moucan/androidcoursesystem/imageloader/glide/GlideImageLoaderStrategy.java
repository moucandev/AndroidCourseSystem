package com.example.moucan.androidcoursesystem.imageloader.glide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.SafeKeyGenerator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.EmptySignature;
import com.pwrd.dls.marble.MyApplication;
import com.pwrd.dls.marble.common.imageloader.IImageLoaderStrategy;
import com.pwrd.dls.marble.common.imageloader.ImageLoader;
import com.pwrd.dls.marble.common.imageloader.glide.transformation.GlideCircleTransform;
import com.pwrd.dls.marble.common.imageloader.listener.ImageLoadListener;
import com.pwrd.dls.marble.common.imageloader.listener.ImageLoadProgressListener;
import com.pwrd.dls.marble.common.imageloader.listener.ImageSaveListener;
import com.pwrd.dls.marble.common.net.upload.ProgressInfo;
import com.pwrd.dls.marble.common.net.upload.ProgressListener;
import com.pwrd.dls.marble.common.net.upload.ProgressManager;
import com.pwrd.dls.marble.common.svg.SvgSoftwareLayerSetter;
import com.pwrd.dls.marble.common.util.BitmapUtils;
import com.pwrd.dls.marble.common.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author xxZhu
 * @class GlideImageLoaderStrategy
 * @describe Glide实现图片加载框架
 */
public class GlideImageLoaderStrategy implements IImageLoaderStrategy {

    private static final String CACHE_PATH=MyApplication.getContext().getExternalCacheDir().getPath()+File.separatorChar+DiskCache.Factory.DEFAULT_DISK_CACHE_DIR;


    @Override
    public void loadImage(Context context,
                          String url,
                          boolean isSvg,
                          boolean isCircle,
                          boolean isCenterCrop,
                          Drawable placeHolder,
                          ImageView imageView, int viewWidth, int viewHeight,
                          ImageLoadListener loadListener,
                          ImageLoadProgressListener loadProgressListener) {

        //构造 RequestOption
        RequestOptions requestOptions=new RequestOptions().skipMemoryCache(true).dontAnimate();
        if(isCircle){
            requestOptions.transform(new GlideCircleTransform(context));
        }
        if(isCenterCrop){
            requestOptions.centerCrop();
        }
        if(placeHolder!=null){
            requestOptions.placeholder(placeHolder);
        }
        if(viewWidth>0&&viewHeight>0){
            requestOptions=requestOptions.override(viewWidth,viewHeight);
        }

        //构造 RequestManager[初始化了GLIDE，加入了RequestManagerFragment]
        RequestManager requestManager=GlideApp.with(context);

        RequestBuilder requestBuilder;
        if(isSvg){
            requestBuilder=requestManager.as(PictureDrawable.class);
        }else{
            requestBuilder=requestManager.asBitmap();
        }

        requestBuilder=requestBuilder.load(url).apply(requestOptions);

        if(isSvg){
            requestBuilder=requestBuilder.addListener(new SvgSoftwareLayerSetter());
        }


        /**--------监听相关--------*/
        if(loadProgressListener!=null){
            String newUrl =new String(url);
            ProgressManager.getInstance().addResponseListener(newUrl, new ProgressListener() {
                @Override
                public void onProgress(ProgressInfo progressInfo) {
                    loadProgressListener.onProgress(progressInfo.getPercent());
                }

                @Override
                public void onError(long id, Exception e) {

                }

                @Override
                public void onSuccess(String result) {

                }
            });
        }

        if(loadListener!=null||loadProgressListener!=null){
            if(isSvg){
                requestBuilder.into(new SimpleTarget<PictureDrawable>(){
                    @Override
                    public void onResourceReady(@NonNull PictureDrawable resource, @Nullable Transition<? super PictureDrawable> transition) {
                        Bitmap bitmap=Bitmap.createBitmap(resource.getIntrinsicWidth(),resource.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas=new Canvas(bitmap);
                        canvas.drawPicture(resource.getPicture());
                        if(loadListener!=null){
                            loadListener.onLoadSuccess(bitmap);
                        }
                        if(loadProgressListener!=null){
                            loadProgressListener.onLoadSuccess(bitmap);
                        }
                        if(imageView!=null){
                            imageView.setImageDrawable(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        if (loadListener != null) {
                            loadListener.onLoadFail(errorDrawable);
                        }
                        if (loadProgressListener != null) {
                            loadProgressListener.onLoadFail(errorDrawable);
                        }
                        if(imageView!=null){
                            imageView.setImageDrawable(errorDrawable);
                        }

                    }

                });
            }else{
                requestBuilder.into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (loadListener != null) {
                            loadListener.onLoadSuccess(resource);
                        }
                        if(loadProgressListener!=null){
                            loadProgressListener.onLoadSuccess(resource);
                        }
                        if(imageView!=null){
                            imageView.setImageBitmap(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        if (loadListener != null) {
                            loadListener.onLoadFail(errorDrawable);
                        }
                        if(loadProgressListener!=null){
                            loadProgressListener.onLoadFail(errorDrawable);
                        }
                        if(imageView!=null){
                            imageView.setImageDrawable(errorDrawable);
                        }
                    }
                });

            }

        }else if(imageView!=null){
            requestBuilder.into(imageView);
        }

    }




    @Override
    public boolean checkImageExist(String url) {
        File file=getCacheFile(url);
        return file!=null;
    }


    /**
     * 获得缓存里的图片【暂不成熟，不开放】
     *
     * 注意！当没有使用Signature的时候可以使用以下方法，同时，如果更改了缓存目录，以下的缓存目录也要改
     *
     * @param url
     * @return
     */
    private File getCacheFile(String url) {
        DataCacheKey dataCacheKey = new DataCacheKey(new GlideUrl(url), EmptySignature.obtain());
        SafeKeyGenerator safeKeyGenerator = new SafeKeyGenerator();
        String safeKey = safeKeyGenerator.getSafeKey(dataCacheKey);
        try {
            int cacheSize = 100 * 1000 * 1000;

            DiskLruCache diskLruCache = DiskLruCache.open(new File(CACHE_PATH), 1, 1, cacheSize);
            DiskLruCache.Value value = diskLruCache.get(safeKey);
            if (value != null) {
                return value.getFile(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void clearImageDiskCache(final Context context) {
        //注意，一定要在后台线程中运行，否则会报错
        GlideApp.get(context.getApplicationContext()).clearDiskCache();


    }

    @Override
    public void clearImageMemoryCache(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                GlideApp.get(context.getApplicationContext()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trimMemory(Context context, int level) {
        Glide.get(context).trimMemory(level);
    }

    @Override
    public long getCacheSize(Context context) {
        try {
            return FileUtils.getFileOrFolderSize(CACHE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void saveImage(Context context,Bitmap bitmap,String savePath,String saveFileName,ImageSaveListener listener){
        if (!FileUtils.isSDCardExsit() || bitmap==null) {
            listener.onSaveFail();
            return;
        }
        Single.create((SingleOnSubscribe<File>)emitter -> {
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, saveFileName+".png");
            try(OutputStream os=new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG,100,os);
                os.close();
                emitter.onSuccess(file);
            }catch (Exception e){
                emitter.onError(e);
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onSuccess(File file) {
                        //用广播通知相册进行更新相册
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        context.sendBroadcast(intent);
                        listener.onSaveSuccess();
                    }
                    @Override
                    public void onError(Throwable e) {
                        listener.onSaveFail();
                    }
                });
    }

    @Override
    public void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener) {
        if (!FileUtils.isSDCardExsit() || TextUtils.isEmpty(url)) {
            listener.onSaveFail();
            return;
        }
        Single.create(new SingleOnSubscribe<File>() {
            @Override
            public void subscribe(SingleEmitter<File> emitter) throws Exception {
                InputStream fromStream = null;
                OutputStream toStream = null;
                try {
                    File cacheFile = Glide.with(context).load(ImageLoader.parseImageUrl(url,0,0,false)).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    if (cacheFile == null || !cacheFile.exists()) {
                        throw new Exception("file is not exist");
                    }
                    File dir = new File(savePath);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File file = new File(dir, saveFileName+ BitmapUtils.getPicType(cacheFile.getAbsolutePath()));
                    fromStream = new FileInputStream(cacheFile);
                    toStream = new FileOutputStream(file);
                    byte length[] = new byte[1024];
                    int count;
                    while ((count = fromStream.read(length)) > 0) {
                        toStream.write(length, 0, count);
                    }
                    emitter.onSuccess(file);

                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(e);
                } finally {
                    if (fromStream != null) {
                        try {
                            fromStream.close();
                            if (toStream != null) {
                                toStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            fromStream = null;
                            toStream = null;
                        }
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(File file) {
                        //用广播通知相册进行更新相册
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        context.sendBroadcast(intent);
                        listener.onSaveSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onSaveFail();
                    }
                });

    }

    @Override
    public void resumeRequest(Context context) {
        Glide.with(context).resumeRequestsRecursive();
    }
}
