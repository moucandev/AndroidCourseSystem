package com.example.moucan.androidcoursesystem.moudle.homepage;

import android.widget.LinearLayout;

import com.example.moucan.androidcoursesystem.R;
import com.example.moucan.androidcoursesystem.base.BaseActivity;
import com.example.moucan.androidcoursesystem.view.TopbarLayout;

import butterknife.BindView;

public class HomeActivity extends BaseActivity {
    @BindView(R.id.top_bar)
    TopbarLayout topbar;
    @BindView(R.id.bottom_navigation)
    LinearLayout bottom_navigation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        topbar.setMainTitle(getResources().getString(R.string.firstPage));

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getTopbarID() {
        return R.id.top_bar;
    }
}
