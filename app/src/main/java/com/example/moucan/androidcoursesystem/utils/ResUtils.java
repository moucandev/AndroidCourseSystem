package com.example.moucan.androidcoursesystem.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;

import com.example.moucan.androidcoursesystem.base.Application;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

public class ResUtils {
    private ResUtils() {
    }

    @SuppressLint("StaticFieldLeak")
    private static final Context context = Application.getContext();

    public static String getString(@StringRes int resId) {
        return context.getString(resId);
    }

    public static String getString(@StringRes int resId, Object... args) {
        return context.getString(resId, args);
    }

    /**
     * 获得字符串资源
     *
     * @param resIds
     * @return
     */
    public static String[] getStringArray(int... resIds) {
        String result[] = new String[resIds.length];
        for (int i = 0; i < resIds.length; i++) {
            result[i] = getString(resIds[i]);
        }
        return result;
    }


    public static Drawable getDrawable(@DrawableRes int resId) {

        return AppCompatResources.getDrawable(context, resId);
    }


    public static Drawable[] getDrawableArray(@DrawableRes int... resIds) {
        Drawable result[] = new Drawable[resIds.length];
        for (int i = 0; i < resIds.length; i++) {
            result[i] = getDrawable(resIds[i]);
        }
        return result;
    }

    /**
     * 获得图片资源
     *
     * @param resId
     * @return
     */
    public static Bitmap getBitmap(int resId) {
        return BitmapFactory.decodeResource(Application.getContext().getResources(), resId);
    }

    public static int getColor(@ColorRes int resId) {
        return context.getResources().getColor(resId);
    }

    public static int getScreenWidth() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getDensity() {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 读取raw
     *
     * @param resId
     * @return
     */
    public static String getFromRaw(int resId) {
        try {
            InputStreamReader inputReader = new InputStreamReader(Application.getContext().getResources().openRawResource(resId));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuilder Result = new StringBuilder();
            while ((line = bufReader.readLine()) != null)
                Result.append(line);
            return Result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取Assets
     *
     * @param fileName
     * @return
     */
    public static String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(Application.getContext().getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder Result = new StringBuilder();
            while ((line = bufReader.readLine()) != null)
                Result.append(line).append("\n");
            return Result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static float dp2px(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static float sp2px(float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    public static int dp2pxInSize(float dpValue) {
        float v = dp2px(dpValue);
        final int res = (int) ((v >= 0) ? (v + 0.5f) : (v - 0.5f));
        if (res != 0) return res;
        if (dpValue == 0) return 0;
        if (dpValue > 0) return 1;
        return -1;

    }

    public static int dp2pxInOffset(float dpValue) {
        float v = dp2px(dpValue);
        return (int) v;
    }


    public static Drawable makeRecDrawable(@ColorInt int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setColor(color);
        return drawable;
    }
}
