package com.example.moucan.androidcoursesystem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class BaseResultActivity extends BaseActivity implements BaseView {

    private static final int NORMAL_STATE = 0;
    private static final int LOADING_STATE = 1;
    public static final int ERROR_STATE = 2;
    public static final int EMPTY_STATE = 3;

    private View mErrorView;
    private View mLoadingView;
    private View mEmptyView;
    private ViewGroup mNormalView;
    private int currentState = NORMAL_STATE;

    @Override
    protected void initView() {
        if(activity == null){
            throw new IllegalStateException("Activity cannot be empty");
        }
        mNormalView = (ViewGroup) findViewById(R.id.content_view);
        if(mNormalView  == null){
            throw new IllegalStateException("There must be no mNormalView in the activity");
        }
        if(!(mNormalView.getParent() instanceof ViewGroup)){
            throw new IllegalStateException("The parent layout of mNormalView must belong to the viewgroup");
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

    @Override
    public void showNormal() {
        if(currentState == NORMAL_STATE){
            return;
        }
        hideCurrentView();
        currentState = NORMAL_STATE;
        mNormalView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String err) {
        if(currentState == ERROR_STATE){
            return;
        }
        hideCurrentView();
        currentState = ERROR_STATE;
        mErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        if(currentState == LOADING_STATE){
            return;
        }
        hideCurrentView();
        currentState = LOADING_STATE;
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        if(currentState == EMPTY_STATE){
            return;
        }
        hideCurrentView();
        currentState = EMPTY_STATE;
        mEmptyView.setVisibility(View.VISIBLE);
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
            default:
                break;
        }
    }

    @Override
    public void reload() {

    }
}

