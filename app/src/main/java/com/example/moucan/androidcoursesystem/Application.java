package com.example.moucan.androidcoursesystem;
import android.content.Context;
import android.os.Handler;

/**
 * @packagename moucan.androidcoursesystem.utils
 * @description 全局上下文
 */
public class Application extends android.app.Application {
    //这是一行没用代码
    public static Context context;
    public static Handler mainHandler;
    private static Application myApplication;



    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        context = this;
        mainHandler = new Handler();
    }
    public static Context getContext() {
        return context;
    }
    public static synchronized Application getInstance() {
        return myApplication;
    }


}
