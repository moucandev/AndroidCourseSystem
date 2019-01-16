package com.example.moucan.androidcoursesystem.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import android.util.AttributeSet;

import com.example.moucan.androidcoursesystem.R;


/**
 * @author xxZhu HXY
 * @class RoundImageView
 * @describe 圆角ImageView
 */
public class RoundImageView extends AppCompatImageView {

    private static final int DEFAULT_RADIUS = 0;
    private Paint mPaint;
    private int mRadius = DEFAULT_RADIUS;
    private BitmapShader shader;
    private Drawable tempDrawable;
    private RectF tempRectF = new RectF();

    public RoundImageView(Context context) {
        super(context);
        init(null);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundImageView);
            mRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_RIV_radius, DEFAULT_RADIUS);
            a.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable mDrawable = getDrawable();
        Matrix mDrawMatrix = getImageMatrix();
        if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }

        if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
            mDrawable.draw(canvas);
        } else {
            final int saveCount = canvas.save();
            if (getCropToPadding()) {
                final int scrollX = getScrollX();
                final int scrollY = getScrollY();
                canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                        scrollX + getRight() - getLeft() - getPaddingRight(),
                        scrollY + getBottom() - getTop() - getPaddingBottom());
            } else {
                canvas.translate(getPaddingLeft(), getPaddingTop());
            }

            //圆角
            if (mRadius > 0) {
                if (tempDrawable != mDrawable) {
                    Bitmap bitmap = drawable2Bitmap(mDrawable);
                    shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                }
                mPaint.setShader(shader);
                tempRectF.set(0, 0, getWidth() - getPaddingRight() - getPaddingLeft(), getHeight() - getPaddingBottom() - getPaddingTop());
                canvas.drawRoundRect(tempRectF, mRadius, mRadius, mPaint);
            }
            //原来的方图
            else {
                tempDrawable = null;
                shader = null;

                if (mDrawMatrix != null) {
                    canvas.concat(mDrawMatrix);
                }
                mDrawable.draw(canvas);
            }
            canvas.restoreToCount(saveCount);
        }
    }


    /**
     * drawable转换成bitmap
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //根据传递的scaletype获取matrix对象，设置给bitmap
        Matrix matrix = getImageMatrix();
        if (matrix != null) {
            canvas.concat(matrix);
        }
        drawable.draw(canvas);
        return bitmap;
    }

}


