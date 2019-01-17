package com.example.moucan.androidcoursesystem.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.example.moucan.androidcoursesystem.base.Application;

public class DrawUtils {

    private static final float scale = Application.getContext().getResources().getDisplayMetrics().density;

    private static final float scaledDensity = Application.getContext().getResources().getDisplayMetrics().scaledDensity;


    /**
     * 获得DisplayMetrics
     * @return
     */
    public static DisplayMetrics getDisplayMetrics()
    {
        return Application.getContext().getResources().getDisplayMetrics();
    }

    /**
     * dp转成px
     *
     * @param dipValue
     *
     * @return
     */
    public static int dip2px(float dipValue) {
        return (int) (dipValue * scale + 0.5f);
//
    }


    /**
     * px转成dp
     *
     * @param pxValue
     *
     * @return
     */
    public static int px2dip(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }




    /**
     * sp转成px
     * @param spValue
     * @param type
     * @return
     */
//    public static float sp2px(float spValue, int type) {
//        switch (type) {
//            case CHINESE:
//                return spValue * scaledDensity;
//            case NUMBER_OR_CHARACTER:
//                return spValue * scaledDensity * 10.0f / 18.0f;
//            default:
//                return spValue * scaledDensity;
//        }
//    }

    /**
     * 获得文字的大小
     * @param text
     * @param textSize
     * @return
     */
    public static int[] getTextWidthHeight(String text, int unit,int textSize)
    {
        Rect rect = new Rect();
        Paint paint=new Paint();
        paint.setTextSize(TypedValue.applyDimension(unit,textSize,Application.getContext().getResources().getDisplayMetrics()));
        paint.getTextBounds(text,0,text.length(), rect);
        int[] result=new int[2];
        result[0] = rect.width();
        result[1] = rect.height();
        return  result;
    }


    /**
     * alpha+baseColor-->COLOR
     * @param alpha
     * @param baseColor
     * @return
     */
    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }










}

