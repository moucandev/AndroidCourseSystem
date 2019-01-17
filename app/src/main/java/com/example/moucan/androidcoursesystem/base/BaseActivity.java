package com.example.moucan.androidcoursesystem.base;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.moucan.androidcoursesystem.receiver.NetWorkBroadcastReceiver;
import com.example.moucan.androidcoursesystem.R;
import com.example.moucan.androidcoursesystem.utils.LogUtils;
import com.example.moucan.androidcoursesystem.utils.NetWorkUtils;
import com.example.moucan.androidcoursesystem.view.OnTopbarClickListener;
import com.example.moucan.androidcoursesystem.view.TopbarLayout;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public abstract class BaseActivity extends SwipeBackActivity implements NetWorkBroadcastReceiver.NetEvent {
    public static NetWorkBroadcastReceiver.NetEvent netEvent;
    public AppDavikActivityUtil appDavikActivityUtil = AppDavikActivityUtil.getScreenManager();
    protected Application context;
    protected BaseActivity activity;
    private View rootView = null;
    private View shadowView = null;
    private TopbarLayout mTopbar;
    private Unbinder bun;
    private SwipeBackLayout swipeBackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        bun = ButterKnife.bind(this);
        appDavikActivityUtil.addActivity(this);
        context = Application.getInstance();
        activity = this;
        netEvent = this;
        initStatusColor();
        if (getTopbarID() != 0) {
            mTopbar = (TopbarLayout) findViewById(getTopbarID());
            mTopbar.setOnTopbarClickListener(mTopbarClickListener);
        }
        swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        initData();
        initView();

    }

    private void initStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            //设置透明状态栏,这样才能让 ContentView 向上  6.0小米手机设置 tootlbar 会被挤上去
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(getColor(R.color.theme));

            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, false);
            }
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract int getTopbarID();

    /**
     * 初始化toolbar
     */
    protected void initToolbar() {
    }

    @Override
    public void onNetChange(String netMobile) {
        if (netMobile.equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
            LogUtils.e("NetWork_Disconnect");
        } else {
            LogUtils.e("NetWork_Normal");
        }
    }


    @Override
    protected void onDestroy() {
        appDavikActivityUtil.removeActivity(this);
        bun.unbind();
        super.onDestroy();
    }

    /**
     * 监听Back键按下事件,默认调用onFinish()
     */
    @Override
    public void onBackPressed() {
        onFinish();
    }

    public void onFinish() {
        finish();
    }

    public TopbarLayout getTopbar() {
        return mTopbar;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mTopbar != null) {
            mTopbar.setMainTitle(title.toString());
        }
    }

    //topbar的监听
    protected OnTopbarClickListener mTopbarClickListener = new OnTopbarClickListener() {
        @Override
        public void onLeftPartClick() {
            onTopbarLeftClick();
        }

        @Override
        public void onRightPartClick() {
            onTopbarRightClick();
        }

        @Override
        public void onRight2PartClick() {
            onTopbarRight2Click();
        }

        @Override
        public void onFunctionPartClick() {
            onTopbarFunctionClick();
        }
    };

    protected void onTopbarRight2Click() {

    }


    protected void onTopbarLeftClick() {
        //默认是finish
        onFinish();
    }

    protected void onTopbarRightClick() {
        //do nothing
    }

    protected void onTopbarFunctionClick() {
        //do nothing
    }


}
