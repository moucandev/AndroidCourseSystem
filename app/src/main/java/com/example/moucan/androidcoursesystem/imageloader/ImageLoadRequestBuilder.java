package com.example.moucan.androidcoursesystem.imageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;


import com.example.moucan.androidcoursesystem.imageloader.listener.ImageLoadListener;
import com.example.moucan.androidcoursesystem.imageloader.listener.ImageLoadProgressListener;
import com.example.moucan.androidcoursesystem.utils.ResUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

/**
 * @author xxZhu
 * @class ImageLoadRequestBuilder
 * @describe
 */
public class ImageLoadRequestBuilder {


    private Context context;


    private UrlRequestOption urlRequestOption;

    private boolean isCircle;

    private Drawable placeHolder;
    private boolean isCenterCrop;

    private ImageView imageView;
    private int viewWidth;
    private int viewHeight;

    private ImageLoadListener loadListener;
    private ImageLoadProgressListener loadProgressListener;

    public ImageLoadRequestBuilder(Context context) {
        this.context = context;
        urlRequestOption = new UrlRequestOption();

    }

    public ImageLoadRequestBuilder url(String url) {
        urlRequestOption.setUrl(url);
        return this;
    }

    public ImageLoadRequestBuilder urlWidthHeight(int urlWidth, int urlHeight) {
        urlRequestOption.setSourceWidth(urlWidth);
        urlRequestOption.setSourceHeight(urlHeight);
        return this;
    }

    public ImageLoadRequestBuilder needCutting() {
        urlRequestOption.setNeedCutting(true);
        return this;
    }

    public ImageLoadRequestBuilder circle() {
        this.isCircle = true;
        return this;
    }

    public ImageLoadRequestBuilder centerCrop() {
        this.isCenterCrop = true;
        return this;
    }

    public ImageLoadRequestBuilder placeHolder(Drawable placeHolder) {
        this.placeHolder = placeHolder;
        return this;
    }

    public ImageLoadRequestBuilder placeHolder(@DrawableRes int drawableId) {
        if (drawableId != 0) {
            this.placeHolder = ResUtils.getDrawable(drawableId);
        }
        return this;
    }

    public ImageLoadRequestBuilder letterWithRandomColor(String letter) {
        this.placeHolder = LetterPlaceholder.makeLetterPlaceholder(context,letter,LetterPlaceholder.getRandomPlaceholderColor());
        return this;
    }

    public ImageLoadRequestBuilder letterWithSpecificColor(String letter, @ColorInt int color) {
        this.placeHolder = LetterPlaceholder.makeLetterPlaceholder(context,letter,color);
        return this;
    }

    public ImageLoadRequestBuilder imageView(ImageView imageView) {
        this.imageView = imageView;
        return this;
    }

    public ImageLoadRequestBuilder viewWidthHeight(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        return this;
    }

    public ImageLoadRequestBuilder imageLoadListener(ImageLoadListener loadListener) {
        this.loadListener = loadListener;
        return this;
    }

    public ImageLoadRequestBuilder imageLoadProgressListener(ImageLoadProgressListener loadListener) {
        this.loadProgressListener = loadListener;
        return this;
    }


    public String load() {
        //处理url
        String finalUrl = urlRequestOption.build();
        //判断svg
        boolean isSvg = finalUrl != null && (finalUrl.contains(".svg") || finalUrl.contains("/svg/"));
        ImageLoader.loadImage(context, finalUrl, isSvg, isCircle, isCenterCrop, placeHolder, imageView, viewWidth, viewHeight, loadListener, loadProgressListener);
        return finalUrl;
    }
}