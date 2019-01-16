package com.example.moucan.androidcoursesystem.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import androidx.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

import com.example.moucan.androidcoursesystem.R;


public class DashedLineView extends View {

    private Paint paint = null;
    private Path path = null;
    private PathEffect effects = null;

    private int orientation;
    private int lineColor=DEFAULT_COLOR_THEME;
    private int dashWidth;      //线段宽度
    private int dashGap;        //线段之间间隔宽度

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private static final int DEFAULT_COLOR_THEME = Color.parseColor("#ff000000");

    public DashedLineView(Context context) {
        super(context);
        init();
    }

    public DashedLineView(Context context, AttributeSet attrs) {
        super(context,attrs);
        setCustomAttributes(attrs);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(lineColor);
        paint.setAntiAlias(true);
        path = new Path();
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DashedLineView);
        lineColor = a.getColor(R.styleable.DashedLineView_lineColor, DEFAULT_COLOR_THEME);
        orientation = a.getInt(R.styleable.DashedLineView_dashOrientation, HORIZONTAL);
        dashWidth = a.getDimensionPixelSize(R.styleable.DashedLineView_dashWidth, 10);
        dashGap = a.getDimensionPixelSize(R.styleable.DashedLineView_dashGap, 10);




        a.recycle();
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        this.invalidate();
    }

    public void setDashWidth(int dashWidth){
        this.dashWidth=dashWidth;
        this.invalidate();
    }

    public void setDashGap(int dashGap){
        this.dashGap=dashGap;
        this.invalidate();
    }


    public void setLineColor(@ColorInt int lineColor) {
        this.lineColor = lineColor;
        paint.setColor(lineColor);
        this.invalidate();
    }

    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(0, 0);

        if (orientation == VERTICAL) {
            //TODO 虚线默认只画了一半，所以这里乘以了2，治标不治本
            paint.setStrokeWidth(getWidth()*2);
            path.lineTo(0, this.getHeight());
        } else {
            paint.setStrokeWidth(getHeight()*2);
            path.lineTo(this.getWidth(), 0);
        }
        // PathEffect是用来控制绘制轮廓(线条)的方式
        // 代码中的float数组,必须是偶数长度,且>=2,指定了多少长度的实线之后再画多少长度的空白.如本代码中,绘制长度5的实线,再绘制长度5的空白,再绘制长度5的实线,再绘制长度5的空白,依次重复.1是偏移量,可以不用理会.
        effects = new DashPathEffect(new float[] { dashWidth, dashGap, dashWidth, dashGap }, 1);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }


}