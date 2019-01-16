package com.example.moucan.androidcoursesystem.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.RequiresApi;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 *  @class      BasePopupWindow
 *  @describe   PopupWindow的定制类(Builder模式)
 *
 *  注意:
 *  1:传入的Context务必是Activity!!!
 *  2.默认window透明度是0.7，具体透明度和进入时间和恢复时间可设置
 *
 */
public    class BasePopupWindow  implements  PopupWindow.OnDismissListener {

    /**
     *  必备参数
     */
    private Context context;
    private int width;
    private int height;

    /**
     *  contentView相关
     */
    private View contentView;
    private int contentViewResId;

    /**
     *  (重要变量！！！)
     *
     *  是否可以通过点击外部 dissmiss popupwindow
     */
    private boolean outsideDissmissEnable=true;


    /**
     *  常用变量
     */
    private boolean focusable = true;
    private boolean outsideTouchable = true;
    private boolean touchable=true;
    private int animationStyle=-1;
    private Drawable backgroudDrawable;

    /**
     *  其他变量
     */
    private boolean clippingEnable=true;
    private boolean ignoreCheekPress=false;

    private int softInputMode = -1;
    private int inputMode=-1;

    /**
     *  屏幕变暗相关
     *
     */
    private float windowDarkAlpha=0.7f;     //默认背景变暗的透明度是0.7,1为全透明
    private int darkInTime=300;             //背景变暗的时间为300ms
    private int darkOutTime=300;            //背景恢复的时间为300ms


    /**
     *  Listener
     */
    private View.OnTouchListener onTouchListener;               //用于设置拦截器(touchListener)
    private PopupWindow.OnDismissListener onDismissListener;


    /**
     *  持有的popupWindow
     */
    private PopupWindow popupWindow;


    /**
     * 私有构造函数
     * @param context
     * @param width
     * @param height
     */
    private BasePopupWindow(Context context,int width,int height)
    {
        this.context=context;
        this.width=width;
        this.height=height;
    }


    /**
     * (重要方法！！！)
     * 根据Builder中设置的参数，实例化一个popupwindow
     * 内部实现了是否可通过外部dissmiss的逻辑
     *
     */
    private void creatPopupWindow()
    {
        if(contentView == null){
            contentView = LayoutInflater.from(context).inflate(contentViewResId,null);
        }
        popupWindow = new PopupWindow(contentView,width,height);


        //添加动画
        if(animationStyle!=-1){
            popupWindow.setAnimationStyle(animationStyle);
        }

        //透明遮罩层
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));


        //设置一些属性
        applyParams(popupWindow);

        // 添加dissmiss 监听
        popupWindow.setOnDismissListener(this);


        //如果要求点击外部不消失的话
        if(!outsideDissmissEnable){
            //注意这三个属性必须同时设置，不然不能disMiss，以下三行代码在Android 4.4 上是可以，然后在Android 6.0以上，下面的三行代码就不起作用了，就得用下面的方法
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(false);
            //注意下面这三个是contentView 不是PopupWindow
            popupWindow.getContentView().setFocusable(true);
            popupWindow.getContentView().setFocusableInTouchMode(true);
            popupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        popupWindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });


            /**
             * 特别注意：
             * 1.event.getX()和event.getY()获得的点击相对坐标是相对于真正的contentView的左上角坐标(可为负)。
             * 2.contentView.getLeft()和contentView.getTop()永远是0，而且getLocationInWindow获得的左上角坐标也都是0，即其是充满屏幕的，这和1有些矛盾，待求解。
             *
             * 判断点击范围是否在contentView内，可使用条件1判断
             *
             */
            //在Android 6.0以上 ，只能通过拦截事件来解决
            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int x = (int) event.getX();
                    final int y = (int) event.getY();

                    if ((event.getAction() == MotionEvent.ACTION_DOWN) && ((x < 0) || (x >= contentView.getWidth()) || (y < 0) || (y >= contentView.getHeight()))) {


                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        return true;
                    }
                    return false;
                }
            });
        }else{
            //要求外部点击消失的话
            popupWindow.setFocusable(focusable);
            popupWindow.setOutsideTouchable(outsideTouchable);
        }

        // update
        popupWindow.update();
    }



    /**
     * 设置其他一些属性
     * @param popupWindow
     */
    private void applyParams(PopupWindow popupWindow){
        if(popupWindow!=null)
        {
            popupWindow.setClippingEnabled(clippingEnable);
            popupWindow.setTouchable(touchable);
            if(ignoreCheekPress){
                popupWindow.setIgnoreCheekPress();
            }
            if(inputMode!=-1){
                popupWindow.setInputMethodMode(inputMode);
            }
            if(softInputMode!=-1){
                popupWindow.setSoftInputMode(softInputMode);
            }
            if(onDismissListener!=null){
                popupWindow.setOnDismissListener(onDismissListener);
            }
            if(onTouchListener!=null){
                popupWindow.setTouchInterceptor(onTouchListener);
            }
        }
    }


    /**
     * 展示相关方法-----------------------------------
     */
    public BasePopupWindow showAsDropDown(View anchor, int xOff, int yOff){
        if(popupWindow!=null&&!popupWindow.isShowing()){

            popupWindow.showAsDropDown(anchor,xOff,yOff);

            if ((windowDarkAlpha>=0.0f)&&(windowDarkAlpha<=1f)&&(darkInTime>=0)){
                applyWindowDarkAlpha(1f, windowDarkAlpha, darkInTime);
            }
        }
        return this;
    }

    public BasePopupWindow showAsDropDown(View anchor){
        if(popupWindow!=null&&!popupWindow.isShowing()){

            popupWindow.showAsDropDown(anchor);

            if ((windowDarkAlpha>=0.0f)&&(windowDarkAlpha<=1f)&&(darkInTime>=0)){
                applyWindowDarkAlpha(1f, windowDarkAlpha, darkInTime);
            }
        }
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public BasePopupWindow showAsDropDown(View anchor, int xOff, int yOff, int gravity){

        if(popupWindow!=null&&!popupWindow.isShowing()){

            popupWindow.showAsDropDown(anchor,xOff,yOff,gravity);

            if ((windowDarkAlpha>=0.0f)&&(windowDarkAlpha<=1f)&&(darkInTime>=0)){
                applyWindowDarkAlpha(1f, windowDarkAlpha, darkInTime);
            }
        }
        return this;
    }


    public BasePopupWindow showAtLocation(View parent, int gravity, int x, int y) {
        if(popupWindow!=null&&!popupWindow.isShowing()){
            popupWindow.showAtLocation(parent, gravity, x, y);
            if ((windowDarkAlpha>=0.0f)&&(windowDarkAlpha<=1f)&&(darkInTime>=0)){
                applyWindowDarkAlpha(1f, windowDarkAlpha, darkInTime);
            }
        }
        return this;
    }

    /**
     * 解决双层popupwindow导致背景显示不正常时调用
     * @return
     */
    public BasePopupWindow getDarkAlpha(){
        if ((windowDarkAlpha>=0.0f)&&(windowDarkAlpha<=1f)&&(darkInTime>=0)){
            applyWindowDarkAlpha(1f, windowDarkAlpha, darkInTime);
        }
        return this;
    }






    @Override
    public void onDismiss() {
        customDissmiss();
    }


    /**
     * popupwindow dissmiss时的操作
     */
    private void customDissmiss()
    {
        if(onDismissListener!=null){
            onDismissListener.onDismiss();
        }

        //如果设置了背景变暗，那么在dissmiss的时候需要还原

        if(windowDarkAlpha<1.0f&&windowDarkAlpha>=0.0f&&(darkOutTime>=0))
        {
            applyWindowDarkAlpha(windowDarkAlpha, 1f, darkOutTime);
        }

        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
        context=null;
    }


    /**
     *  @class      Builder
     *  @author     xxZhu
     *  @describe   Builder模式
     *
     */
    public static class Builder{

        private BasePopupWindow myBasePopupWindow;

        public Builder(Context context,int width,int height)
        {
            myBasePopupWindow=new BasePopupWindow(context,width,height);
        }

        public Builder setContentView(int resLayoutId){
            myBasePopupWindow.contentViewResId = resLayoutId;
            myBasePopupWindow.contentView = null;
            return this;
        }

        public Builder setContentView(View view){
            myBasePopupWindow.contentView = view;
            myBasePopupWindow.contentViewResId = -1;
            return this;
        }


        public Builder setFocusable(boolean focusable)
        {
            myBasePopupWindow.focusable=focusable;
            return this;
        }

        public Builder setOutsideTouchable(boolean outsideTouchable)
        {
            myBasePopupWindow.outsideTouchable=outsideTouchable;
            return this;
        }

        public Builder setTouchable(boolean touchable)
        {
            myBasePopupWindow.touchable=touchable;
            return this;
        }

        public Builder setAnimationStyle(int animationStyle)
        {
            myBasePopupWindow.animationStyle=animationStyle;
            return this;
        }

        public Builder setBackgroudDrawable(Drawable backgroudDrawable)
        {
            myBasePopupWindow.backgroudDrawable=backgroudDrawable;
            return this;
        }

        public Builder setClippingEnable(boolean clippingEnable)
        {
            myBasePopupWindow.clippingEnable=clippingEnable;
            return this;
        }

        public Builder setIgnoreCheekPress(boolean ignoreCheekPress)
        {
            myBasePopupWindow.ignoreCheekPress=ignoreCheekPress;
            return this;
        }

        public Builder setInputMode(int  inputMode)
        {
            myBasePopupWindow.inputMode=inputMode;
            return this;
        }

        public Builder setSoftInputMode(int  softInputMode)
        {
            myBasePopupWindow.softInputMode=softInputMode;
            return this;
        }


        public Builder setWindowDarkAlpha(float windowDarkAlpha)
        {
            if(windowDarkAlpha<0.0f)
            {
                myBasePopupWindow.windowDarkAlpha=0.0f;
            }else if(windowDarkAlpha>1.0f)
            {
                myBasePopupWindow.windowDarkAlpha=1.0f;
            }else{
                myBasePopupWindow.windowDarkAlpha=windowDarkAlpha;
            }

            return this;
        }


        public Builder setDarkInTime(int darkInTime)
        {
            myBasePopupWindow.darkInTime = darkInTime;
            return this;
        }

        public Builder setDarkOutTime(int darkOutTime)
        {
            myBasePopupWindow.darkOutTime = darkOutTime;
            return this;
        }


        public Builder setTouchIntercepter(View.OnTouchListener touchIntercepter){
            myBasePopupWindow.onTouchListener = touchIntercepter;
            return this;
        }

        public Builder setOnDissmissListener(PopupWindow.OnDismissListener onDissmissListener){
            myBasePopupWindow.onDismissListener = onDissmissListener;
            return this;
        }

        /**
         * 设置是否允许点击 PopupWindow之外的地方，关闭PopupWindow
         * @param outsideDissmissEnable 是否可以通过点击外部 dissmiss PopupWindow
         * @return
         */
        public Builder setOutsideDissmissEnable(boolean outsideDissmissEnable){
            myBasePopupWindow.outsideDissmissEnable = outsideDissmissEnable;
            return this;
        }



        public BasePopupWindow bulid()
        {
            myBasePopupWindow.creatPopupWindow();
            return myBasePopupWindow;
        }

    }//builder结束


    private void applyWindowDarkAlpha(float from, float to, int duration) {
        final Window window=((Activity)context).getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.alpha = (float) animation.getAnimatedValue();
                window.setAttributes(lp);
            }
        });
        animator.start();
    }


    /**
     * 判断是否在展示
     *
     * @return the boolean
     */
    public boolean isShowing()
    {
        if(popupWindow!=null&&popupWindow.isShowing())
        {
            return true;
        }else
        {
            return false;
        }
    }


    /**
     * popupwindow的dissmiss
     */
    public void dismiss()
    {
        if(popupWindow!=null&&popupWindow.isShowing())
        {
            popupWindow.dismiss();
        }
    }

}

