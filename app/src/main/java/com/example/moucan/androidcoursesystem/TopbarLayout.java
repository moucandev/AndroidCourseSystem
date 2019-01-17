package com.example.moucan.androidcoursesystem;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moucan.androidcoursesystem.utils.DrawUtils;

public class TopbarLayout extends FrameLayout implements View.OnClickListener {

    private final static int DEFAULT_MAIN_TITLE_SINGLE_ROW_SIZE_DP = 18;
    private final static int DEFAULT_MAIN_TITLE_DOUBLE_ROW_SIZE_DP = 17;

    private final static int DEFAULT_TOP_LAYOUT_SINGLE_ROW_MARGIN_TOP_PX = DrawUtils.dip2px(10);
    private final static int DEFAULT_TOP_LAYOUT_DOUBLE_ROW_MARGIN_TOP_PX = DrawUtils.dip2px(6);


    private ImageView img_left;
    private ImageView img_right;
    private ImageView img_right2;

    private TextView tv_mainTitle;

    private TextView tv_funcTitle;
    private TextView tv_subTitle;
    private TextView text_topbar_right;

    private View division;

    private CharSequence mainTitleStr;
    private String subTitleStr;
    private String funcTitleStr;
    private String rightTextStr;

    private Drawable leftDrawable;
    private Drawable rightDrawable;

    private boolean showDivision;


    private OnTopbarClickListener mOnTopbarClickListener;
    private boolean hasInit;
    private Drawable right2Drawable;


    public TopbarLayout(Context context) {

        this(context, null);
    }

    public TopbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
        initView(context);
        updateView(context);
    }


    /**
     * 获得属性
     *
     * @param context
     * @param attrs
     */
    private void getAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopbarLayout);
            leftDrawable = ta.getDrawable(R.styleable.TopbarLayout_topbar_leftImage);
            rightDrawable = ta.getDrawable(R.styleable.TopbarLayout_topbar_rightImage);
            mainTitleStr = ta.getString(R.styleable.TopbarLayout_topbar_mainTitle);
            subTitleStr = ta.getString(R.styleable.TopbarLayout_topbar_subTitle);
            funcTitleStr = ta.getString(R.styleable.TopbarLayout_topbar_funcTitle);
            rightTextStr = ta.getString(R.styleable.TopbarLayout_topbar_rightText);
            showDivision = ta.getBoolean(R.styleable.TopbarLayout_topbar_showDivision, false);
            ta.recycle();
        }
    }


    /**
     * 根据attrs 初始化view
     *
     * @param context
     */
    private void initView(Context context) {
        View topbar = LayoutInflater.from(context).inflate(R.layout.layout_topbar, this, true);
        img_left = topbar.findViewById(R.id.img_topbar_left);
        img_right = topbar.findViewById(R.id.img_topbar_right);
        img_right2 = topbar.findViewById(R.id.img_topbar_right_2);
        tv_mainTitle = topbar.findViewById(R.id.tv_topbar_mainTitle);
        tv_funcTitle = topbar.findViewById(R.id.tv_topbar_functionTitle);
        tv_subTitle = topbar.findViewById(R.id.tv_topbar_subTitle);
        text_topbar_right = topbar.findViewById(R.id.text_topbar_right);
        division = topbar.findViewById(R.id.division);


        hasInit = true;
    }

    private void updateView(Context context) {
        if (!hasInit) {
            initView(context);
        }
        //subTitle相关
        if (subTitleStr == null) {
            tv_subTitle.setVisibility(GONE);
            tv_mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MAIN_TITLE_SINGLE_ROW_SIZE_DP);

        } else {
            tv_subTitle.setVisibility(VISIBLE);
            tv_subTitle.setText(subTitleStr);
            tv_mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MAIN_TITLE_DOUBLE_ROW_SIZE_DP);
        }

        //function相关
        if (funcTitleStr == null) {
            tv_funcTitle.setVisibility(View.GONE);
        } else {
            tv_funcTitle.setText(funcTitleStr);
            tv_funcTitle.setVisibility(VISIBLE);
            tv_funcTitle.setOnClickListener(this);
        }

        //mainTitle相关
        if (mainTitleStr == null) {
            tv_mainTitle.setVisibility(GONE);
        } else {
            tv_mainTitle.setText(mainTitleStr);
            tv_mainTitle.setVisibility(VISIBLE);
        }

        //左右图片
        if (leftDrawable != null) {
            img_left.setImageDrawable(leftDrawable);
            img_left.setOnClickListener(this);
        } else {
            img_left.setOnClickListener(null);
        }
        if (rightDrawable != null) {
            img_right.setImageDrawable(rightDrawable);
            img_right.setOnClickListener(this);
        } else {
            img_right.setOnClickListener(null);
            img_right.setImageDrawable(null);
        }
        if (right2Drawable != null) {
            img_right2.setImageDrawable(right2Drawable);
            img_right2.setOnClickListener(this);
        } else {
            img_right2.setOnClickListener(null);
            img_right2.setImageDrawable(null);
        }
        if (!TextUtils.isEmpty(rightTextStr)) {
            text_topbar_right.setText(rightTextStr);
            text_topbar_right.setOnClickListener(this);
            img_right.setVisibility(View.INVISIBLE);
            text_topbar_right.setVisibility(View.VISIBLE);
        } else {
            img_right.setVisibility(View.VISIBLE);
            text_topbar_right.setVisibility(View.INVISIBLE);
            text_topbar_right.setOnClickListener(null);
        }
        //分界线
        division.setVisibility(showDivision ? VISIBLE : GONE);
    }

    /**
     * 设置相关标题
     */
    public void setMainTitle(CharSequence mainTitle) {
        mainTitleStr = mainTitle;
        updateView(getContext());
    }

    public void setSubTitle(String subTitle) {
        subTitleStr = subTitle;
        updateView(getContext());
    }

    public void setFuncTitle(String funcTitle) {
        funcTitleStr = funcTitle;
        updateView(getContext());
    }

    public void setTitles(String mainTitle, String subTitle, String funcTitle) {
        mainTitleStr = mainTitle;
        subTitleStr = subTitle;
        funcTitleStr = funcTitle;
        updateView(getContext());
    }

    public void setLeftImage(Drawable drawable) {
        leftDrawable = drawable;
        updateView(getContext());
    }

    public void setRightImage(Drawable drawable) {
        rightDrawable = drawable;
        updateView(getContext());
    }

    public void setRight2Image(Drawable drawable) {
        right2Drawable = drawable;
        updateView(getContext());
    }

    public void setRightText(String rightText) {
        rightTextStr = rightText;
        updateView(getContext());
    }

    public void setRightVisibility(int rightVisibility) {
        img_right.setVisibility(rightVisibility);
    }

    public void setEnable(boolean leftEnable, boolean rightEnable) {
        if (img_left != null) {
            img_left.setEnabled(leftEnable);
        }
        if (img_left != null) {
            img_right.setEnabled(leftEnable);
        }
    }

    /**
     * 设置监听回调
     *
     * @param onTopbarClickListener 监听回调
     */
    public void setOnTopbarClickListener(OnTopbarClickListener onTopbarClickListener) {
        this.mOnTopbarClickListener = onTopbarClickListener;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_topbar_left:
                if (mOnTopbarClickListener != null)
                    mOnTopbarClickListener.onLeftPartClick();
                break;
            case R.id.img_topbar_right:
            case R.id.text_topbar_right:
                if (mOnTopbarClickListener != null)
                    mOnTopbarClickListener.onRightPartClick();
                break;
            case R.id.img_topbar_right_2:
                if (mOnTopbarClickListener != null)
                    mOnTopbarClickListener.onRight2PartClick();
                break;
            case R.id.tv_topbar_functionTitle:
                if (mOnTopbarClickListener != null)
                    mOnTopbarClickListener.onFunctionPartClick();
                break;
        }
    }


}

