package com.example.moucan.androidcoursesystem.view;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;


import com.example.moucan.androidcoursesystem.R;
import com.example.moucan.androidcoursesystem.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class SimpleSlidingPanel extends FrameLayout {
    /**
     * 顶部空隙
     */
    private View simpleslidingpanel_topgap = null;
    /**
     * 把手区域
     */
    private FrameLayout simpleslidingpanel_handle = null;
    /**
     * 面板区域
     */
    private FrameLayout simpleslidingpanel_panel = null;
    /**
     * 面板上部区域
     */
    private FrameLayout simpleslidingpanel_panel_topsection = null;
    /**
     * 面板下部区域
     */
    private FrameLayout simpleslidingpanel_panel_bottomsection = null;
    /**
     * 面板的遮盖界面
     */
    private FrameLayout fl_simpleslidingpanel_cover = null;
    /**
     * 面板loading
     */
    private FrameLayout fl_simpleslidingpanel_panel_loading = null;

    public static final int PANELSTATE_FULLSCREEN = 3;
    public static final int PANELSTATE_PARTIAL = 2;
    public static final int PANELSTATE_DESCENDED = 1;
    private static final int PANELSTATE_SCROLLING = 0;
    public static final int PANELSTATE_UP2FULLSCREEN = 4;
    public static final int PANELSTATE_UP2PARTIAL = 5;
    public static final int PANELSTATE_DOWN2PARTIAL = 6;
    public static final int PANELSTATE_DOWN2DESCENDED = 7;
    public static final int PANELSTATE_HIDDEN = 8;
    /**
     * 面板状态
     * 1-descended，下降收起；2-partial，部分展示；3-fullscreen，全部展示
     */
    private int panelState = PANELSTATE_PARTIAL;

    /**
     * 滑动面板控件总宽度
     */
    private int totalWidth = 0;
    /**
     * 滑动面板控件总高度
     */
    private int totalHeight = 0;

    /**
     * 面板partial状态高度占fullscreen状态高度的百分比，不含把手部分
     */
    private float panelPartialStateRatio = 0.45f;
    /**
     * 面板partial状态实际高度
     */
    private int panelPartialStateHeight = -1;
    /**
     * 全部展示状态时的顶部空隙高度
     */
    private float gapHeight = 0;
    /**
     * 把手区域的高度
     */
    private float handleHeight = 0;

    /**
     * 当前面板区域的Y方向位置
     */
    private float currentPanelY = 0;

    /**
     * 把手区域隐藏时渐变动画
     */
    private AlphaAnimation alphaAnimation = null;

    /**
     * 面板假想Y方向位置
     */
    private float currentPanelFakedY = 0;

    /**
     * 内部可滑动子类的滑动监听器
     */
    private InnerScrollSensor innerScrollSensor = null;

    /**
     * 面板滑动监听器
     */
    private PanelScrollListener panelScrollListener = null;
    private List<PanelScrollListener> panelScrollListenerList = new ArrayList<>();
    private int scaledTouchSlop;

    public SimpleSlidingPanel setPanelScrollListener(PanelScrollListener panelScrollListener) {
        this.panelScrollListener = panelScrollListener;
        return this;
    }

    public SimpleSlidingPanel addPanelScrollListener(PanelScrollListener panelScrollListener) {
        panelScrollListenerList.add(panelScrollListener);
        return this;
    }

    public SimpleSlidingPanel removePanelScrollListener(PanelScrollListener panelScrollListener) {
        panelScrollListenerList.remove(panelScrollListener);
        return this;
    }


    public SimpleSlidingPanel(@NonNull Context context) {
        this(context, null);
    }

    public SimpleSlidingPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleSlidingPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attrsInit(context, attrs, defStyleAttr, 0);
        UIinit(context);
    }

    @TargetApi(21)
    public SimpleSlidingPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        attrsInit(context, attrs, defStyleAttr, defStyleRes);
        UIinit(context);
    }

    /**
     * 自定义属性初始化
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    private void attrsInit(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleSlidingPanel, defStyleAttr, defStyleRes);

        panelState = typedArray.getInt(R.styleable.SimpleSlidingPanel_SSP_state, PANELSTATE_PARTIAL);
        panelPartialStateRatio = typedArray.getFloat(R.styleable.SimpleSlidingPanel_SSP_partialstate_ratio, 0.45f);
        gapHeight = typedArray.getDimension(R.styleable.SimpleSlidingPanel_SSP_gap_height, 0);
        handleHeight = typedArray.getDimension(R.styleable.SimpleSlidingPanel_SSP_handle_height, 0);

        typedArray.recycle();
    }

    /**
     * 布局初始化
     *
     * @param context
     */
    private void UIinit(Context context) {
        //加载面板布局
        View.inflate(context, R.layout.layout_simpleslidingpanel, this);
        //获取子部件引用
        simpleslidingpanel_topgap = findViewById(R.id.simpleslidingpanel_topgap);
        simpleslidingpanel_handle = new FrameLayout(context);
        simpleslidingpanel_handle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });     //把手截获手势响应
        simpleslidingpanel_panel = (FrameLayout) findViewById(R.id.simpleslidingpanel_panel);
        simpleslidingpanel_panel_topsection = (FrameLayout) findViewById(R.id.simpleslidingpanel_panel_topsection);
        simpleslidingpanel_panel_bottomsection = (FrameLayout) findViewById(R.id.simpleslidingpanel_panel_bottomsection);
        fl_simpleslidingpanel_panel_loading = (FrameLayout) findViewById(R.id.fl_simpleslidingpanel_panel_loading);
        fl_simpleslidingpanel_panel_loading.setVisibility(View.GONE);
        fl_simpleslidingpanel_cover = (FrameLayout) findViewById(R.id.fl_simpleslidingpanel_cover);
        fl_simpleslidingpanel_cover.setVisibility(View.GONE);   //cover默认不展示

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        scaledTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        totalHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    /**
     * 是否为第一次布局
     */
    private boolean isFirstLayout = true;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (isFirstLayout) {
            UISetUp();  //UISetUp()必须在onLayout中执行，若在onMeasure（会执行两次）中，会由于中间改变子布局参数导致后续错误
            isFirstLayout = false;
        }


    }

    /**
     * 子布局初始设置
     */
    private void UISetUp() {
        if (panelPartialStateHeight < 0) {
            panelPartialStateHeight = (int) (totalHeight * panelPartialStateRatio);
        }

        if (!descendedEnabled) {
            handleHeight = panelPartialStateHeight;
        }

        //根据属性参数设置各子部件状态
        simpleslidingpanel_topgap.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) gapHeight));
        simpleslidingpanel_handle.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) handleHeight));
        simpleslidingpanel_panel.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (totalHeight - gapHeight) + 1));

        switch (panelState) {
            case PANELSTATE_FULLSCREEN:     //全部展示
                currentPanelY = gapHeight;
                break;
            case PANELSTATE_PARTIAL:        //部分展示
                currentPanelY = totalHeight - panelPartialStateHeight;
                break;
            case PANELSTATE_DESCENDED:      //下降收起
                currentPanelY = totalHeight - handleHeight;
                setHandleVisibility(true);
                break;
            default:
                currentPanelY = totalHeight;//异常时收回面板
                break;
        }

        setPanelY(currentPanelY);
        currentPanelFakedY = currentPanelY;
    }

    //panel高度变化时，loading的位置始终保持垂直居中的位置，并重新设置cover位置
    private void setPanelY(float panelY) {
        simpleslidingpanel_panel.setY(panelY);

        fl_simpleslidingpanel_panel_loading.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (totalHeight - panelY)));

        if (isCoverInCenter) {
            fl_simpleslidingpanel_cover.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (totalHeight - panelY)));
        } else {
            fl_simpleslidingpanel_cover.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }

    //cover是否始终位于面板可视区域中间的标识
    private boolean isCoverInCenter = false;

    /**
     * 设置面板遮盖界面内容
     *
     * @param coverView       遮盖view
     * @param backgroundColor 遮盖层背景颜色
     * @param alwaysInCenter  遮盖view是否始终位于面板可视区域的中间位置
     */
    public void setPanelCover(View coverView, @ColorInt int backgroundColor, boolean alwaysInCenter) {
        if (coverView == null) {
            return;
        }
        fl_simpleslidingpanel_cover.removeAllViews();
        fl_simpleslidingpanel_cover.addView(coverView);
        fl_simpleslidingpanel_cover.setBackgroundColor(backgroundColor);
        isCoverInCenter = alwaysInCenter;
        if (isCoverInCenter) {
            LayoutParams lp = (LayoutParams) coverView.getLayoutParams();
            lp.gravity = Gravity.CENTER;
            coverView.setLayoutParams(lp);
        }
    }

    /**
     * 展示面板遮盖界面
     */
    public void showPanelCover() {
        fl_simpleslidingpanel_cover.setVisibility(View.VISIBLE);

        setPanelY(currentPanelY);
    }

    /**
     * 隐藏面板遮盖界面
     */
    public void hidePanelCover() {
        fl_simpleslidingpanel_cover.setVisibility(View.GONE);
    }

    public void setContentElevation(@ColorInt int color, float dpValue) {
        simpleslidingpanel_panel.setBackgroundColor(color);
        ViewCompat.setElevation(simpleslidingpanel_panel, ResUtils.dp2px(dpValue));
    }

    public float getPanelPartialStateRatio() {
        return panelPartialStateRatio;
    }

    public int getPanelPartialStateHeight() {
        return panelPartialStateHeight;
    }

    public void startLoading() {
        startLoading(Color.TRANSPARENT);
    }

    public void startLoading(@ColorInt int loadingBackgroundColor) {
        fl_simpleslidingpanel_panel_loading.setBackgroundColor(loadingBackgroundColor);
        fl_simpleslidingpanel_panel_loading.setVisibility(View.VISIBLE);
    }

    public void endLoading() {
        fl_simpleslidingpanel_panel_loading.setVisibility(View.GONE);
    }


    /**
     * 设置面板部分展示状态的高度
     *
     * @param height 部分展示状态的实际高度
     * @return
     */
    public SimpleSlidingPanel setPartialStateHeight(int height) {
        panelPartialStateHeight = height;

        if (!descendedEnabled) {
            handleHeight = panelPartialStateHeight;
        }

        if (isFirstLayout) {
            return this;
        }

        panelState = PANELSTATE_PARTIAL;

        currentPanelY = simpleslidingpanel_panel.getY();
        float targetPanelY = totalHeight - panelPartialStateHeight;

        float v = (targetPanelY - currentPanelY) / 1.0f;
        gotoPosition(targetPanelY, v);

        return this;
    }

    /**
     * 设置面板部分展示状态的高度
     *
     * @param ratio 部分展示状态实际高度占fullscreen状态高度的百分比
     * @return
     */
    public SimpleSlidingPanel setPartialStateHeight(float ratio) {
        if (ratio > 1 || ratio < 0) {
            ratio = panelPartialStateRatio;
        }

        panelPartialStateRatio = ratio;

        if (isFirstLayout) {
            return this;
        }
        return setPartialStateHeight((int) (totalHeight * panelPartialStateRatio));
    }

    /**
     * 设置把手区域高度
     *
     * @param height
     * @return
     */
    public SimpleSlidingPanel setHandleHeight(float height) {
        this.handleHeight = height;

        if (simpleslidingpanel_handle != null) {
            simpleslidingpanel_handle.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) handleHeight));

            if (panelState == PANELSTATE_DESCENDED) {
//                down2Descended();
            }
        }

        return this;
    }

    /**
     * 设置 InnerScrollSensor
     *
     * @param scrollSensor
     */
    public void setInnerScrollSensor(InnerScrollSensor scrollSensor) {
        this.innerScrollSensor = scrollSensor;
    }

    public InnerScrollSensor getInnerScrollSensor() {
        return innerScrollSensor;
    }

    /**
     * 设置面板上部区域显示内容
     *
     * @param view
     * @param sensor
     * @return
     */
    public SimpleSlidingPanel setPanelTopContent(View view, InnerScrollSensor sensor) {
        if (simpleslidingpanel_panel_topsection != null) {
            //清空View
            if (viewOnTopPanel != null) {
                simpleslidingpanel_panel_topsection.removeView(viewOnTopPanel);
            }
            //清空Fragment
            if (fragmentManager != null && fragmentOnTopPanel != null) {
                fragmentManager.beginTransaction().hide(fragmentOnTopPanel).commitAllowingStateLoss();
                fragmentOnTopPanel = null;
            }

            viewOnTopPanel = view;

            if (viewOnTopPanel != null) {
                simpleslidingpanel_panel_topsection.addView(viewOnTopPanel);
            }

            innerScrollSensor = sensor;
        }

        return this;
    }

    /**
     * 当前面板上部显示的Fragment
     */
    private Fragment fragmentOnTopPanel = null;
    /**
     * 跨方法使用的FragmentManager
     */
    private FragmentManager fragmentManager = null;
    /**
     * 当前面板上部显示的View
     */
    private View viewOnTopPanel = null;

    /**
     * 设置面板上部区域显示内容
     *
     * @param manager
     * @param fragment
     * @param sensor
     * @return
     */
    public SimpleSlidingPanel setPanelTopContent(@NonNull FragmentManager manager, Fragment fragment, InnerScrollSensor sensor) {
        fragmentManager = manager;

        if (simpleslidingpanel_panel_topsection != null) {
            //清空View
            if (viewOnTopPanel != null) {
                simpleslidingpanel_panel_topsection.removeView(viewOnTopPanel);
                viewOnTopPanel = null;
            }
            //清空Fragment
            if (fragmentOnTopPanel != null) {
                manager.beginTransaction().hide(fragmentOnTopPanel).commitAllowingStateLoss();
            }

            fragmentOnTopPanel = fragment;

            if (fragmentOnTopPanel != null) {
                if (!fragmentOnTopPanel.isAdded()) {
                    manager.beginTransaction().add(simpleslidingpanel_panel_topsection.getId(), fragmentOnTopPanel).commitAllowingStateLoss();
                }
                manager.beginTransaction().show(fragmentOnTopPanel).commitAllowingStateLoss();
            }

            innerScrollSensor = sensor;
        }

        return this;
    }

    /**
     * 设置面板下部区域显示内容
     *
     * @param view
     * @param sensor
     * @return
     */
    public SimpleSlidingPanel setPanelBottomContent(View view, InnerScrollSensor sensor) {
        if (simpleslidingpanel_panel_bottomsection != null) {
            //清空View
            if (viewOnBottomPanel != null) {
                simpleslidingpanel_panel_bottomsection.removeView(viewOnBottomPanel);
            }
            //清空Fragment
            if (fragmentManager != null && fragmentOnBottomPanel != null) {
                fragmentManager.beginTransaction().hide(fragmentOnBottomPanel).commitAllowingStateLoss();
                fragmentOnBottomPanel = null;
            }

            viewOnBottomPanel = view;

            if (viewOnBottomPanel != null) {
                simpleslidingpanel_panel_bottomsection.addView(viewOnBottomPanel);
            }

            innerScrollSensor = sensor;
        }

        return this;
    }

    /**
     * 当前面板下部显示的Fragment
     */
    private Fragment fragmentOnBottomPanel = null;
    /**
     * 当前面板下部显示的View
     */
    private View viewOnBottomPanel = null;

    /**
     * 设置面板下部区域显示内容
     *
     * @param manager
     * @param fragment
     * @param sensor
     * @return
     */
    public SimpleSlidingPanel setPanelBottomContent(@NonNull FragmentManager manager, Fragment fragment, InnerScrollSensor sensor) {
        fragmentManager = manager;

        if (simpleslidingpanel_panel_bottomsection != null) {
            //清空View
            if (viewOnBottomPanel != null) {
                simpleslidingpanel_panel_bottomsection.removeView(viewOnBottomPanel);
                viewOnBottomPanel = null;
            }
            //清空Fragment
            if (fragmentOnBottomPanel != null) {
                fragmentOnBottomPanel.setUserVisibleHint(false);
                manager.beginTransaction().hide(fragmentOnBottomPanel).commitNowAllowingStateLoss();
            }

            fragmentOnBottomPanel = fragment;

            if (fragmentOnBottomPanel != null) {
                if (!fragmentOnBottomPanel.isAdded()) {
                    manager.beginTransaction().add(simpleslidingpanel_panel_bottomsection.getId(), fragmentOnBottomPanel).commitNowAllowingStateLoss();
                }else {
                    manager.beginTransaction().show(fragmentOnBottomPanel).commitNowAllowingStateLoss();
                }
                fragmentOnBottomPanel.setUserVisibleHint(true);
            }

            innerScrollSensor = sensor;
        }

        return this;
    }

    /**
     * 内部可滑动子类的滑动传感器
     */
    public interface InnerScrollSensor {
        /**
         * 获得当前子类自身的滑动偏移
         *
         * @return 偏移量，>=0
         */
        public float getCurrentScrollOffset();

        /**
         * 通知子类复位接口
         */
        public void reStore();
    }

    /**
     * 面板滑动监听器
     */
    public interface PanelScrollListener {
        /**
         * 滑动监听回调方法
         *
         * @param panelHeightRatio 面板当前高度占控件总高度的百分比
         * @param currentState     当前面板状态
         */
        public void onScroll(float panelHeightRatio, int currentState);

        /**
         * 滑动结束回调方法
         *
         * @param panelState 面板状态
         */
        public void onScrollFinished(int panelState);
    }

    /**
     * 设置把手区域显示视图
     *
     * @param view
     * @return
     */
    public SimpleSlidingPanel setHandleContent(View view) {
        if (simpleslidingpanel_handle != null) {
            simpleslidingpanel_handle.removeAllViews();
            if (view != null) {
                simpleslidingpanel_handle.addView(view);
            }
        }

        return this;
    }

    /**
     * 设置全部展示状态时的顶部空隙高度
     *
     * @param gap
     * @return
     */
    public SimpleSlidingPanel setTopGap(int gap) {
        gapHeight = gap;

        if (simpleslidingpanel_topgap != null) {
            simpleslidingpanel_topgap.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, gap));
        }

        return this;
    }

    public SimpleSlidingPanel setTopGap(int unit, float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();
        return setTopGap((int) TypedValue.applyDimension(unit, size, r.getDisplayMetrics()));
    }

    private boolean hasHandle = true;

    public void setHandleAppearOrNotWhenDescended(boolean appearOrNot) {
        hasHandle = appearOrNot;
    }

    /**
     * 显示、隐藏把手区域接口
     *
     * @param visible
     * @return
     */
    public SimpleSlidingPanel setHandleVisibility(boolean visible) {
        if (simpleslidingpanel_handle != null && hasHandle) {
            if (visible)     //显示把手
            {
                if (!simpleslidingpanel_handle.isShown()) {
                    simpleslidingpanel_panel.addView(simpleslidingpanel_handle);
                }
            } else            //隐藏把手
            {
                if (simpleslidingpanel_handle.isShown()) {
                    if (alphaAnimation == null) {
                        alphaAnimationInit();
                    }
                    simpleslidingpanel_handle.startAnimation(alphaAnimation);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            simpleslidingpanel_panel.removeView(simpleslidingpanel_handle);
                        }
                    }, 100);
                }
            }
        }

        return this;
    }

    /**
     * 把手淡出动画
     */
    private void alphaAnimationInit() {
        alphaAnimation = new AlphaAnimation(1.0f, 0);
        alphaAnimation.setDuration(100);
    }

    /**
     * targetView为在SimpleSlidingPanel不响应手势时，计划响应该手势的子类对象
     */
    private View targetView = null;

    private VelocityTracker mVelocityTracker = null;    //速度检测器
    private boolean interceptEvent = false;             //是否截获手势事件标识
    private boolean touchOnPanel = false;               //是否按住面板区域标识
    private boolean isPanelEnabled = true;              //面板是否拦截手势标识
    private boolean hasChildHorizontalScrolled = false; //面板子类是否横向滑动过标识

    public boolean isPanelEnabled() {
        return isPanelEnabled;
    }

    public void setPanelEnabled(boolean panelEnabled) {
        isPanelEnabled = panelEnabled;
    }

    private float fingerX_Old = 0;
    private float fingerY_Old = 0;
    private float PanelY_Old = 0;   //用作手势位移计算，为按下时面板位置基准值

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (panelState == PANELSTATE_SCROLLING) {
            //处在滑动过程中的Panel不响应手势事件
            return false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                targetView = null;

                fingerX_Old = ev.getX();
                fingerY_Old = ev.getY();

                hasChildHorizontalScrolled = false;

                currentPanelY = simpleslidingpanel_panel.getY();

                //手势记录位置与假想位置对齐
                PanelY_Old = currentPanelFakedY;

                if (fingerY_Old >= currentPanelY)    //按在了面板区域
                {
                    touchOnPanel = true;

                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(ev);
                } else        //未按在面板区域
                {
                    touchOnPanel = false;
                }

                //巡查并获得计划响应该手势的面板子类对象
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    if (getChildAt(i).dispatchTouchEvent(ev)) {
                        targetView = getChildAt(i);
                        break;
                    }
                }
                return touchOnPanel;        //只有ACTION_DOWN时返回true，之后的事件才会继续传递到SimpleSlidingPanel来

            case MotionEvent.ACTION_MOVE:
                if (!interceptEvent)     //不截此事件
                {
                    //判断手指位移距离，若横向滑动、或停住不动，则进入else分支，下发事件；
                    //若纵向滑动且已经大于一定距离，则交由自己响应该事件，并设置interceptEvent = true，之后的ACTION_MOVE事件都会被拦截并交由自己响应
                    if (isPanelEnabled
                            && Math.abs(ev.getY() - fingerY_Old) > Math.abs(ev.getX() - fingerX_Old)
                            && Math.abs(ev.getY() - fingerY_Old) > scaledTouchSlop
                            && !hasChildHorizontalScrolled) {
                        interceptEvent = true;
                        return onTouchEvent(ev);
                    } else {
                        if (targetView != null) {
                            if (!descendedEnabled)   //无收起状态
                            {
                                if (Math.abs(ev.getX() - fingerX_Old) > scaledTouchSlop) {
                                    hasChildHorizontalScrolled = true;
                                }
                                return targetView.dispatchTouchEvent(ev);
                            } else    //有收起状态
                            {
                                //panelState != PANELSTATE_DESCENDED的意义是保证：收起状态时，不下发任何事件给子类处理
//                                if(panelState == PANELSTATE_DESCENDED)
//                                {
//                                    return false;
//                                }
//                                else
//                                {
                                if (Math.abs(ev.getX() - fingerX_Old) > scaledTouchSlop) {
                                    hasChildHorizontalScrolled = true;
                                }
                                return targetView.dispatchTouchEvent(ev);
//                                }
                            }
                        } else {
                            return false;
                        }
                    }
                } else        //截获此事件
                {
                    return onTouchEvent(ev);
                }

            case MotionEvent.ACTION_UP:
                touchOnPanel = false;
                if (interceptEvent) {
                    interceptEvent = false;    //重置事件截获标识
                    return onTouchEvent(ev);
                } else {
                    if (targetView != null) {
                        if (!descendedEnabled)   //无收起状态
                        {
                            return targetView.dispatchTouchEvent(ev);
                        } else    //有收起状态
                        {
                            //panelState != PANELSTATE_DESCENDED的意义是保证：收起状态时，不下发任何事件给子类处理
//                            if(panelState != PANELSTATE_DESCENDED)
//                            {
                            return targetView.dispatchTouchEvent(ev);
//                            }
//                            else
//                            {
//                                return false;
//                            }
                        }
                    } else {
                        return false;
                    }
                }

            case MotionEvent.ACTION_CANCEL:
                //释放速度检测器
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                return false;

            default:
                return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //一旦响应面板滑动，就移除把手区域
                setHandleVisibility(false);

                float dy = ev.getY() - fingerY_Old;
                currentPanelFakedY = currentPanelY = PanelY_Old + dy;

                //已经移到顶部
                if (currentPanelY <= gapHeight) {
                    currentPanelY = gapHeight;

                    //移到顶部后则向子类下发事件
                    if (targetView != null) {
                        targetView.dispatchTouchEvent(ev);
                    }
                } else {
                    if (innerScrollSensor != null) {
                        /**
                         * 对面板内部子类滑动的校准
                         * 由于内部滑动子类在处理手势时，滑动距离与手指移动距离未必是1:1的关系
                         * 这将导致假想位置与真实位置重合时，子类的滑动未必归零，因此在这里进行校准
                         */
                        if (innerScrollSensor.getCurrentScrollOffset() != 0) {
                            innerScrollSensor.reStore();
                        }
                    }
                }

                //已经移到底部
                if (currentPanelY >= totalHeight - handleHeight) {
                    currentPanelY = totalHeight - handleHeight;
                    currentPanelFakedY = currentPanelY;
                }

                setPanelY(currentPanelY);
                informUser(PANELSTATE_SCROLLING, false);
                mVelocityTracker.addMovement(ev);
                return true;

            case MotionEvent.ACTION_UP:
                currentPanelY = simpleslidingpanel_panel.getY();

                //移进顶部的情况
                if (currentPanelFakedY < currentPanelY) {
                    panelState = PANELSTATE_FULLSCREEN;

                    /**
                     * innerScrollSensor != null 是保证在需要嵌套滚动时，才继续这部分逻辑的执行，否则会触发其它类型的子类事件响应（如点击事件）
                     *
                     * 此时将抬起事件下发给子类，可以让子类完成自身的惯性触发
                     *
                     * 此时，currentPanelFakedY将与currentPanelY产生差异，该差异的作用将被用在下次手势ACTION_DOWN时，将手势处理的位置与假想位置对齐
                     * 这样可以将两次手势完成的 移进移出顶部 与一次手势完成的 移进移出顶部 策略合并，共用一套代码逻辑
                     */
                    if (innerScrollSensor != null) {
                        if (targetView != null) {
                            targetView.dispatchTouchEvent(ev);

                            //根据子类的回调，同步自身的面板假想Y位置
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    float targetFakedY = currentPanelY - innerScrollSensor.getCurrentScrollOffset();
                                    if (currentPanelFakedY != targetFakedY) {
                                        currentPanelFakedY = targetFakedY;
                                        postDelayed(this, 16);
                                    }
                                }
                            }, 16);
                        }
                    } else {
                        currentPanelFakedY = currentPanelY;
                    }

                    return true;
                }

                //其它常规情况
                panelState = PANELSTATE_SCROLLING;

                mVelocityTracker.addMovement(ev);
                mVelocityTracker.computeCurrentVelocity(1000);

                if (mVelocityTracker.getYVelocity() >= 0)    //手势为向下移动
                {
                    if (currentPanelY >= gapHeight && currentPanelY < totalHeight - panelPartialStateHeight)     //面板位于上半部分
                    {
                        down2Partial();
                    } else if (currentPanelY >= totalHeight - panelPartialStateHeight && currentPanelY <= totalHeight - handleHeight)  //面板位于下半部分
                    {
                        down2Descended();
                    }
                } else        //手势为向上移动
                {
                    if (currentPanelY >= gapHeight && currentPanelY < totalHeight - panelPartialStateHeight)     //面板位于上半部分
                    {
                        up2Fullscreen();
                    } else if (currentPanelY >= totalHeight - panelPartialStateHeight && currentPanelY <= totalHeight - handleHeight)  //面板位于下半部分
                    {
                        up2Partial();
                    }
                }

                return true;
            default:
                return super.onTouchEvent(ev);
        }
    }

    /**
     * 通知使用者，执行PanelScrollListener回调
     *
     * @param state
     * @param isFinished
     */
    private void informUser(int state, boolean isFinished) {
        if (panelScrollListener != null) {
            if (!isFinished) {
                panelScrollListener.onScroll((totalHeight - currentPanelY) / totalHeight, state);

            } else {
                panelScrollListener.onScroll((totalHeight - currentPanelY) / totalHeight, state);
                panelScrollListener.onScrollFinished(state);

            }
        }
        if (!panelScrollListenerList.isEmpty()) {
            if (!isFinished) {
                for (PanelScrollListener scrollListener : panelScrollListenerList) {
                    scrollListener.onScroll((totalHeight - currentPanelY) / totalHeight, state);
                }
            } else {
                for (PanelScrollListener scrollListener : panelScrollListenerList) {
                    scrollListener.onScroll((totalHeight - currentPanelY) / totalHeight, state);
                    scrollListener.onScrollFinished(state);
                }
            }
        }
    }

    /**
     * 向上滑到全部展示状态
     */
    private void up2Fullscreen() {
        post(new Runnable() {
            @Override
            public void run() {
                currentPanelY -= 80;

                if (currentPanelY > gapHeight) {
                    setPanelY(currentPanelY);
                    informUser(PANELSTATE_UP2FULLSCREEN, false);   //告知使用者

                    postDelayed(this, 16);
                } else {
                    currentPanelY = gapHeight;
                    setPanelY(currentPanelY);

                    panelState = PANELSTATE_FULLSCREEN;
                    currentPanelFakedY = currentPanelY;

                    informUser(panelState, true);
                }
            }
        });
    }

    /**
     * 向下滑到收起状态
     */
    private void down2Descended() {
        post(new Runnable() {
            @Override
            public void run() {
                currentPanelY += 80;

                if (currentPanelY < totalHeight - handleHeight)      //尚未滑到底部
                {
                    setPanelY(currentPanelY);
                    informUser(PANELSTATE_DOWN2DESCENDED, false);   //告知使用者

                    postDelayed(this, 16);
                } else            //已经滑到底部
                {
                    currentPanelY = totalHeight - handleHeight;
                    setPanelY(currentPanelY);

                    panelState = PANELSTATE_DESCENDED;
                    currentPanelFakedY = currentPanelY;

                    setHandleVisibility(true);

                    informUser(panelState, true);
                }
            }
        });
    }

    /**
     * 向上滑到部分展示状态
     */
    private void up2Partial() {
        post(new Runnable() {
            @Override
            public void run() {
                currentPanelY -= 80;

                if (currentPanelY > totalHeight - panelPartialStateHeight) {
                    setPanelY(currentPanelY);
                    informUser(PANELSTATE_UP2PARTIAL, false);   //告知使用者

                    postDelayed(this, 16);
                } else {
                    currentPanelY = totalHeight - panelPartialStateHeight;
                    setPanelY(currentPanelY);

                    panelState = PANELSTATE_PARTIAL;
                    currentPanelFakedY = currentPanelY;

                    informUser(panelState, true);
                }
            }
        });
    }

    /**
     * 向下滑到部分展示状态
     */
    private void down2Partial() {
        post(new Runnable() {
            @Override
            public void run() {
                currentPanelY += 80;

                if (currentPanelY < totalHeight - panelPartialStateHeight) {
                    setPanelY(currentPanelY);
                    informUser(PANELSTATE_DOWN2PARTIAL, false);   //告知使用者

                    postDelayed(this, 16);
                } else {
                    currentPanelY = totalHeight - panelPartialStateHeight;
                    setPanelY(currentPanelY);

                    panelState = PANELSTATE_PARTIAL;
                    currentPanelFakedY = currentPanelY;

                    informUser(panelState, true);
                }
            }
        });
    }

    /**
     * 设置滑动面板经过多长时间滑到指定状态
     *
     * @param newPanelState 目标状态
     * @param timeMillis    滑动时间 0表示直接到达 单位：毫秒
     * @return
     */
    public SimpleSlidingPanel setStateDelayed(int newPanelState, long timeMillis) {
        //若面板正处于滑动状态，则等待其结束后继续执行
        while (panelState == PANELSTATE_SCROLLING) {
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (panelState == newPanelState) {
            return this;
        }

        float targetPanelY = 0;
        float v;

        switch (newPanelState) {
            case PANELSTATE_FULLSCREEN:
                targetPanelY = gapHeight;
                break;
            case PANELSTATE_PARTIAL:
                targetPanelY = totalHeight - panelPartialStateHeight;
                break;
            case PANELSTATE_DESCENDED:
                targetPanelY = totalHeight - handleHeight;
                break;
            default:
                return this;
        }

        panelState = newPanelState;

        currentPanelY = simpleslidingpanel_panel.getY();
        if (timeMillis <= 0) {
            timeMillis = 1;
        }
        v = (targetPanelY - currentPanelY) / timeMillis;

        //重置内部子类的滑动状态
        if (innerScrollSensor != null) {
            if (innerScrollSensor.getCurrentScrollOffset() != 0) {
                innerScrollSensor.reStore();
            }
        }

        //开始响应面板滑动前，移除把手区域
        setHandleVisibility(false);

        gotoPosition(targetPanelY, v);

        return this;
    }

    /**
     * 面板滑动到指定位置
     *
     * @param position 目标位置，单位：px
     * @param velocity 滑动速度，单位：px/ms
     */
    private void gotoPosition(final float position, float velocity) {
        currentPanelY = simpleslidingpanel_panel.getY();
        final float movingStep = velocity * 16;     //刷新周期为16ms，因此步长为velocity * 16

        post(new Runnable() {
            @Override
            public void run() {
                currentPanelY += movingStep;

                if (movingStep >= 0)     //moving down
                {
                    if (currentPanelY < position) {
                        setPanelY(currentPanelY);

                        if (panelState == PANELSTATE_PARTIAL) {
                            informUser(PANELSTATE_DOWN2PARTIAL, false);
                        } else if (panelState == PANELSTATE_DESCENDED) {
                            informUser(PANELSTATE_DOWN2DESCENDED, false);
                        }

                        postDelayed(this, 16);
                    } else {
                        currentPanelY = position;
                        setPanelY(currentPanelY);

                        currentPanelFakedY = currentPanelY;

                        if (panelState == PANELSTATE_DESCENDED) {
                            setHandleVisibility(true);  //显示把手
                        }

                        informUser(panelState, true);

                        isAnimating = false;

                        if (!blockingQueue.isEmpty()) {
                            blockingQueue.poll().run();
                        }
                    }
                } else        //moving up
                {
                    if (currentPanelY > position) {
                        setPanelY(currentPanelY);

                        if (panelState == PANELSTATE_PARTIAL) {
                            informUser(PANELSTATE_UP2PARTIAL, false);
                        } else if (panelState == PANELSTATE_FULLSCREEN) {
                            informUser(PANELSTATE_UP2FULLSCREEN, false);
                        }

                        postDelayed(this, 16);
                    } else {
                        currentPanelY = position;
                        setPanelY(currentPanelY);

                        currentPanelFakedY = currentPanelY;

                        informUser(panelState, true);

                        isAnimating = false;

                        if (!blockingQueue.isEmpty()) {
                            blockingQueue.poll().run();
                        }
                    }
                }
            }
        });
    }

    /**
     * 获得面板状态
     *
     * @return
     */
    public int getPanelState() {
        return panelState;
    }

    /**
     * 获得把手高度
     *
     * @return
     */
    public float getHandleHeight() {
        return handleHeight;
    }

    private boolean descendedEnabled = true;

    /**
     * 设置是否包含收起模式[不能下滑到底]
     *
     * @param enabled
     */
    public void setDescendedEnabled(boolean enabled) {
        descendedEnabled = enabled;
    }

    /**
     * 判断Panel当前是否处于动画执行期间的标识
     */
    private boolean isAnimating = false;
    /**
     * 面板切换动效队列
     */
    private final BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(25);
    /**
     * resetPanelState()这个接口调用及执行的计数标识
     */
    private int resetNotResponsedCount = 0;

    /**
     * 执行抽屉动画
     *
     * @param durationMillis 单程耗时，单位：ms
     */
    public void executeDrawerAnimation(long durationMillis) {
        if (panelState == PANELSTATE_SCROLLING || isAnimating) {
            return;
        }

        isAnimating = true;

        final float oldPanelY = currentPanelY;
        float velocity = (totalHeight - oldPanelY) / durationMillis;
        final float movingStep = velocity * 16;

        post(new Runnable() {
            boolean isDowning = true;

            @Override
            public void run() {
                if (isDowning) {
                    currentPanelY += movingStep;

                    if (currentPanelY < totalHeight) {
                        setPanelY(currentPanelY);
                    } else {
                        currentPanelY = totalHeight;
                        setPanelY(currentPanelY);
                        isDowning = false;
                    }

                    postDelayed(this, 16);
                } else {
                    currentPanelY -= movingStep;

                    if (currentPanelY > oldPanelY) {
                        setPanelY(currentPanelY);
                        postDelayed(this, 16);
                    } else {
                        currentPanelY = oldPanelY;
                        setPanelY(currentPanelY);

                        isAnimating = false;

                        if (!blockingQueue.isEmpty()) {
                            blockingQueue.poll().run();
                        }
                    }
                }
            }
        });
    }

    private final int DefaultMarkValue = -1;
    private float PanelY_Mark = DefaultMarkValue;

    /**
     * 隐藏面板
     *
     * @param durationMillis 隐藏滑动动画时长
     */
    public void hidePanel(long durationMillis) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (panelState == PANELSTATE_SCROLLING || PanelY_Mark != DefaultMarkValue)   //hidePanel执行后，若未调用showPanel，则再次调用直接返回
                {
                    return;
                }

                isAnimating = true;

                PanelY_Mark = currentPanelY;
                float velocity = (totalHeight - PanelY_Mark) / durationMillis;
                final float movingStep = velocity * 16;

                post(new Runnable() {
                    @Override
                    public void run() {
                        currentPanelY += movingStep;

                        if (currentPanelY < totalHeight) {
                            setPanelY(currentPanelY);
                            postDelayed(this, 16);

                            informUser(panelState, false);
                        } else {
                            currentPanelY = totalHeight;
                            setPanelY(currentPanelY);

                            isAnimating = false;

                            if (!blockingQueue.isEmpty()) {
                                blockingQueue.poll().run();
                            }
                            informUser(PANELSTATE_HIDDEN, true);
                        }
                    }
                });
            }
        };

        if (isAnimating) {
            blockingQueue.offer(runnable);

            return;
        } else {
            runnable.run();
        }
    }

    /**
     * 显示面板
     *
     * @param durationMillis 显示滑动动画时长
     */
    public void showPanel(long durationMillis) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (panelState == PANELSTATE_SCROLLING || PanelY_Mark == DefaultMarkValue)   //未调用过hidePanel，调用showPanel直接返回
                {
                    return;
                }

                isAnimating = true;

                float velocity = (PanelY_Mark - totalHeight) / durationMillis;
                final float movingStep = velocity * 16;

                post(new Runnable() {
                    @Override
                    public void run() {
                        currentPanelY += movingStep;

                        if (currentPanelY > PanelY_Mark) {
                            setPanelY(currentPanelY);
                            postDelayed(this, 16);

                            informUser(panelState, false);
                        } else {
                            currentPanelY = PanelY_Mark;
                            setPanelY(currentPanelY);
                            PanelY_Mark = DefaultMarkValue;

                            isAnimating = false;

                            if (!blockingQueue.isEmpty()) {
                                blockingQueue.poll().run();
                            }
                            informUser(panelState, true);
                        }
                    }
                });
            }
        };

        if (isAnimating) {
            blockingQueue.offer(runnable);

            return;
        } else {
            runnable.run();
        }
    }

    /**
     * 判断面板当前是否显示
     *
     * @return
     */
    public boolean isShown() {
        if (PanelY_Mark == DefaultMarkValue) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 面板状态切换类型常量
     */
    public static final int PANEL_TRANSITION_TYPE_DIRECT = 0x11111110;
    public static final int PANEL_TRANSITION_TYPE_SMOOTH = 0x11111111;
    public static final int PANEL_TRANSITION_TYPE_DRAWER = 0x11111112;

    /**
     * 面板状态重置的状态切换监听
     */
    public interface PanelStateResetListener {
        public void onTransitionStart();

        public void onTransitionProcessing();

        public void onTransitionFinish();
    }

    /**
     * <p>重置面板状态</p>
     * <p>可以为面板设置新的PartialStateHeight，同时可以设置新面板的最终呈现状态（并不一定是PANELSTATE_PARTIAL）</p>
     * <p>切换类型是面板从旧状态变化到新状态的过程动效类型</p>
     * <p>包含DIRECT-直接呈现最终状态、SMOOTH-平滑移动至最终状态、DRAWER-执行抽屉效果，先hide再show至最终状态</p>
     * <p>用户可以通过设置PanelStateResetListener的方式告诉面板在切换状态的过程中需要同时处理的行为</p>
     *
     * @param targetPanelState      目标面板状态
     * @param newPartialStateHeight 新的部分展示状态下的高度
     * @param transitionType        状态切换类型
     * @param durationMillis        状态切换单程时长
     * @param actionWithin          状态切换过程中需要执行的用户行为
     */
    public void resetPanelState(int targetPanelState, int newPartialStateHeight, int transitionType, long durationMillis, PanelStateResetListener actionWithin) {

        if (isFirstLayout) {
            panelPartialStateHeight = newPartialStateHeight;
            panelState = targetPanelState;
            return;
        }

        resetNotResponsedCount++;

        if (durationMillis <= 0) {
            durationMillis = 1;
        }
        final long timeMillis = durationMillis;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isAnimating = true;
                resetNotResponsedCount--;

                panelPartialStateHeight = newPartialStateHeight;
                if (!descendedEnabled) {
                    handleHeight = panelPartialStateHeight;
                }

                float targetPanelY = 0;     //目标最终位置

                switch (targetPanelState) {
                    case PANELSTATE_FULLSCREEN:
                        targetPanelY = gapHeight;
                        break;
                    case PANELSTATE_PARTIAL:
                        targetPanelY = totalHeight - panelPartialStateHeight;
                        break;
                    case PANELSTATE_DESCENDED:
                        targetPanelY = totalHeight - handleHeight;
                        break;
                    default:
                        targetPanelY = totalHeight - panelPartialStateHeight;   //异常数据默认为部分展示状态
                        break;
                }

                switch (transitionType) {
                    case PANEL_TRANSITION_TYPE_DIRECT:
                        currentPanelY = currentPanelFakedY = targetPanelY;
                        setPanelY(currentPanelY);
                        panelState = targetPanelState;
                        if (actionWithin != null) {
                            if (resetNotResponsedCount == 0) {
                                actionWithin.onTransitionStart();
                                actionWithin.onTransitionProcessing();
                                actionWithin.onTransitionFinish();
                            }
                        }
                        isAnimating = false;
                        if (!blockingQueue.isEmpty()) {
                            blockingQueue.poll().run();
                        }
                        break;

                    case PANEL_TRANSITION_TYPE_SMOOTH:
                        if (actionWithin != null) {
                            if (resetNotResponsedCount == 0) {
                                actionWithin.onTransitionStart();
                                actionWithin.onTransitionProcessing();
                                actionWithin.onTransitionFinish();
                            }
                        }
                        currentPanelY = simpleslidingpanel_panel.getY();
                        float v = (targetPanelY - currentPanelY) / timeMillis;
                        panelState = targetPanelState;
                        gotoPosition(targetPanelY, v);
                        break;

                    case PANEL_TRANSITION_TYPE_DRAWER:
                        currentPanelY = simpleslidingpanel_panel.getY();
                        float v_down = (totalHeight - currentPanelY) / timeMillis;
                        final float movingStep_down = v_down * 16;
                        float v_up = (targetPanelY - totalHeight) / timeMillis;
                        final float movingStep_up = v_up * 16;
                        final float targetY = targetPanelY;
                        panelState = targetPanelState;

                        if (actionWithin != null) {
                            if (resetNotResponsedCount == 0) {
                                actionWithin.onTransitionStart();
                            }
                        }

                        post(new Runnable() {
                            float movingStep = movingStep_down;
                            boolean isDowning = true;

                            @Override
                            public void run() {
                                currentPanelY += movingStep;

                                if (isDowning) {
                                    if (currentPanelY < totalHeight) {
                                        setPanelY(currentPanelY);
                                        postDelayed(this, 16);

                                        informUser(PANELSTATE_DOWN2DESCENDED, false);
                                    } else {
                                        currentPanelY = totalHeight;
                                        setPanelY(currentPanelY);
                                        isDowning = false;
                                        movingStep = movingStep_up;
                                        postDelayed(this, 16);
                                        if (actionWithin != null) {
                                            if (resetNotResponsedCount == 0) {
                                                actionWithin.onTransitionProcessing();
                                            }
                                        }
                                        informUser(PANELSTATE_DOWN2DESCENDED, false);
                                    }
                                } else {
                                    if (currentPanelY > targetY) {
                                        setPanelY(currentPanelY);
                                        postDelayed(this, 16);

                                        informUser(PANELSTATE_UP2PARTIAL, false);
                                    } else {
                                        currentPanelY = currentPanelFakedY = targetY;
                                        setPanelY(currentPanelY);
                                        if (actionWithin != null) {
                                            if (resetNotResponsedCount == 0) {
                                                actionWithin.onTransitionFinish();
                                            }
                                        }

                                        informUser(panelState, true);

                                        isAnimating = false;
                                        if (!blockingQueue.isEmpty()) {
                                            blockingQueue.poll().run();
                                        }
                                    }
                                }
                            }
                        });
                        break;

                    default:    //异常数据采用默认的PANEL_TRANSITION_TYPE_DIRECT情况
                        currentPanelY = currentPanelFakedY = targetPanelY;
                        setPanelY(currentPanelY);
                        panelState = targetPanelState;
                        if (actionWithin != null) {
                            if (resetNotResponsedCount == 0) {
                                actionWithin.onTransitionStart();
                                actionWithin.onTransitionProcessing();
                                actionWithin.onTransitionFinish();
                            }
                        }
                        isAnimating = false;
                        if (!blockingQueue.isEmpty()) {
                            blockingQueue.poll().run();
                        }
                        break;
                }
            }
        };

        if (isAnimating) {
            blockingQueue.offer(runnable);

            return;
        } else {
            runnable.run();
        }
    }

    public void resetPanelState(int targetPanelState, float newPartialStateHeightRatio, int transitionType, long durationMillis, PanelStateResetListener actionWithin) {
        if (newPartialStateHeightRatio > 1 || newPartialStateHeightRatio < 0) {
            newPartialStateHeightRatio = panelPartialStateRatio;
        }

        panelPartialStateRatio = newPartialStateHeightRatio;

        if (isFirstLayout) {
            panelState = targetPanelState;
        } else {
            resetPanelState(targetPanelState, (int) (totalHeight * panelPartialStateRatio), transitionType, durationMillis, actionWithin);
        }
    }
}
