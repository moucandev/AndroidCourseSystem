package com.example.moucan.androidcoursesystem.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.moucan.androidcoursesystem.R;
import com.example.moucan.androidcoursesystem.receiver.NetWorkBroadcastReceiver;
import com.example.moucan.androidcoursesystem.utils.NetWorkUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment implements BaseView, NetWorkBroadcastReceiver.NetEvent{
    protected Activity activity;
    protected Application context;
    private Unbinder bind;
    public static NetWorkBroadcastReceiver.NetEvent eventFragment;
    private String  netMobile;

    /**
     * 处理页面加载中、页面加载失败、页面没数据
     */
    private static final int NORMAL_STATE = 0;
    private static final int LOADING_STATE = 1;
    public static final int ERROR_STATE = 2;
    public static final int EMPTY_STATE = 3;

    private View mErrorView;
    private View mLoadingView;
    private View mEmptyView;
    private ViewGroup mNormalView;
    /**
     * 当前状态
     */
    private int currentState = NORMAL_STATE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResID(), container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = Application.getInstance();
        eventFragment = this;
        bind = ButterKnife.bind(this, view);
        initData();
        initView();

    }

    /**
     * 网络改变的监听
     * @param netMobile
     */
    @Override
    public void onNetChange(String netMobile) {
        this.netMobile = netMobile;
        isNetConnect();
    }

    @Override
    public void onDestroy() {
        bind.unbind();
        super.onDestroy();
    }

    /**
     * 获取 布局信息
     * @return
     */
    public abstract int getLayoutResID();

    /**
     * 数据初始化
     */
    protected abstract void initData();

    /**
     * 初始化 ui 布局
     */
    protected void initView(){
        if (getView() == null) {
            return;
        }
        mNormalView = getView().findViewById(R.id.content_view);
        if (mNormalView == null) {
            throw new IllegalStateException("The subclass of RootActivity must contain a View named 'mNormalView'.");
        }
        if (!(mNormalView.getParent() instanceof ViewGroup)) {
            throw new IllegalStateException("mNormalView's ParentView should be a ViewGroup.");
        }
        ViewGroup parent = (ViewGroup) mNormalView.getParent();
        View.inflate(activity, R.layout.layout_loading, parent);
        View.inflate(activity, R.layout.layout_error, parent);
        View.inflate(activity, R.layout.layout_empty, parent);
        mLoadingView = parent.findViewById(R.id.loading_normal);
        mErrorView = parent.findViewById(R.id.error_normal);
        mEmptyView = parent.findViewById(R.id.empty_normal);
        mErrorView.setOnClickListener(v -> reload());
        mErrorView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mNormalView.setVisibility(View.VISIBLE);
    }

    /**
     * 判断有无网络
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netMobile == NetWorkUtils.NETWORK_TYPE_WIFI) {
            return true;
        } else if (netMobile == NetWorkUtils.NETWORK_TYPE_2G) {
            return true;
        }else if (netMobile == NetWorkUtils.NETWORK_TYPE_3G) {
            return true;
        } else if (netMobile == NetWorkUtils.NETWORK_TYPE_DISCONNECT) {
            return false;
        }
        return false;
    }

    @Override
    public void showNormal() {
        if (currentState == NORMAL_STATE) {
            return;
        }
        hideCurrentView();
        currentState = NORMAL_STATE;
        mNormalView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String err) {
        if (currentState == ERROR_STATE) {
            return;
        }
        hideCurrentView();
        currentState = ERROR_STATE;
        mErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        if (currentState == LOADING_STATE) {
            return;
        }
        hideCurrentView();
        currentState = LOADING_STATE;
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        if (currentState == EMPTY_STATE) {
            return;
        }
        hideCurrentView();
        currentState = EMPTY_STATE;
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void reload() {
        initData();
        initView();
    }

    private void hideCurrentView() {
        switch (currentState) {
            case NORMAL_STATE:
                if (mNormalView == null) {
                    return;
                }
                mNormalView.setVisibility(View.GONE);
                break;
            case LOADING_STATE:
                mLoadingView.setVisibility(View.GONE);
                break;
            case ERROR_STATE:
                mErrorView.setVisibility(View.GONE);
                break;
            case EMPTY_STATE:
                mEmptyView.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

}
