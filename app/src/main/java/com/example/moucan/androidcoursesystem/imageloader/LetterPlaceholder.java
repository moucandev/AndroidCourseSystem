package com.example.moucan.androidcoursesystem.imageloader;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Layout;
import android.text.TextUtils;

import com.example.moucan.androidcoursesystem.R;
import com.example.moucan.androidcoursesystem.imageloader.listener.ILetterPlaceholder;
import com.example.moucan.androidcoursesystem.utils.ResUtils;

import java.io.Serializable;

import androidx.annotation.ColorInt;

/**
 *  @class      LetterPlaceholder
 *  @describe   需要
 *
 */
public    class LetterPlaceholder implements ILetterPlaceholder,Serializable {

    private static int currentColorCursor = 0;

    private @ColorInt
    int mPlaceholderColor=PLACEHOLDER_COLOR[0];


    @Override
    public void fillColorRandom(){
        mPlaceholderColor=getRandomPlaceholderColor();
    }
    @Override
    public int getPlaceholderColor() {
        return mPlaceholderColor;
    }


    /**---------------STATIC METHOD---------------*/
    public static final @ColorInt
    int getRandomPlaceholderColor(){
        return PLACEHOLDER_COLOR[(currentColorCursor++) % PLACEHOLDER_COLOR.length];
    }

    public static final @ColorInt
    int getInitPlaceholderColor(){
        return PLACEHOLDER_COLOR[0];
    }

    public static final Drawable makeLetterPlaceholder(Context context,String letter,@ColorInt int bgColor) {
        if(!TextUtils.isEmpty(letter)){
            letter=letter.substring(0,1);
        }
        TextDrawable textDrawable = new TextDrawable(context);
        textDrawable.setText(letter);
        textDrawable.setTextColor(ResUtils.getColor(R.color.white));
        textDrawable.setTypeface(Typeface.DEFAULT_BOLD);
        textDrawable.setTextSize(25);
        textDrawable.setTextAlign(Layout.Alignment.ALIGN_CENTER);

        ColorDrawable colorDrawable = new ColorDrawable(bgColor);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{colorDrawable, textDrawable});
        return layerDrawable;
    }

}
