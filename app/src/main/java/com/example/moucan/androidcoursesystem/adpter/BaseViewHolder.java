package com.example.moucan.androidcoursesystem.adpter;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moucan.androidcoursesystem.R;
import com.example.moucan.androidcoursesystem.imageloader.ImageLoader;
import com.example.moucan.androidcoursesystem.utils.ResUtils;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

public    class BaseViewHolder extends RecyclerView.ViewHolder   {

    private SparseArray<View> mViews=new SparseArray<View>();
    private View mItemView;
    private Context mContext;


    public BaseViewHolder(View itemView,Context context) {
        super(itemView);
        this.mItemView=itemView;
        this.mContext=context;
    }

    public Context  getContext(){
        return mContext;
    }

    public static BaseViewHolder creatViewHolder(Context context, int layoutId, ViewGroup parent)
    {
        View itemView= LayoutInflater.from(context).inflate(layoutId,parent,false);
        return new BaseViewHolder(itemView,context);
    }

    public View getItemView()
    {
        return mItemView;
    }

    public <T extends View>  T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mItemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public <T extends View>  T getViewTry(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            try{
                view = mItemView.findViewById(viewId);
                mViews.put(viewId, view);
            }catch (Exception e){

            }
        }
        return (T) view;
    }


    public BaseViewHolder setVisibility(int viewId, int visibility)
    {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }


    /**---------------ClickListener相关-------------------*/

    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener)
    {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnClickListener(int viewId ,int duration , View.OnClickListener listener)
    {

        View view = getView(viewId);
        setOnClickListener(viewId,duration,listener);
        return this;
    }

    public BaseViewHolder setOnClickListener(View.OnClickListener listener)
    {
        itemView.setOnClickListener(listener);
        return this;
    }



    /**---------------Text相关-------------------*/
    public BaseViewHolder setText(int viewId, CharSequence text)
    {
        if(text==null)
        {
            text="";
        }
        TextView tv = getView(viewId);
        if(TextUtils.isEmpty(text)) {
            tv.setVisibility(View.GONE);
        }else {
//            text= StringUtils.trimLine(text);
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
        }
        return this;
    }




    public BaseViewHolder setTextVisible(@IdRes int viewId, CharSequence text)
    {
        if(text==null)
        {
            text="";
        }
        TextView tv = getView(viewId);
//        text= StringUtils.trimLine(text);
        tv.setText(text);
        return this;
    }

    public BaseViewHolder setTextColor(@IdRes int viewId,@ColorRes int colorId)
    {
        TextView tv = getView(viewId);
        tv.setTextColor(ResUtils.getColor(colorId));
        return this;
    }


    /**---------------- Image 相关-----------*/

    public BaseViewHolder setImageResource(@IdRes int viewId,@DrawableRes int resId)
    {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public BaseViewHolder setImageBackgroud(@IdRes int viewId, @DrawableRes int resId)
    {

        ImageView view = getView(viewId);
        view.setBackgroundResource(resId);
        return this;
    }

    public BaseViewHolder setImageUrl(@IdRes int viewId, String imageUrl)
    {
        ImageView view = getView(viewId);
        if(TextUtils.isEmpty(imageUrl))
        {
            view.setVisibility(View.GONE);
        }else
        {
            view.setVisibility(View.VISIBLE);
            ImageLoader.with(mContext)
                    .url(imageUrl)
                    .placeHolder(R.mipmap.init_pic)
                    .imageView(view)
                    .load();

        }
        return this;
    }

    public BaseViewHolder setImageUrlVisible(@IdRes int viewId, String imageUrl)
    {
        ImageView view = getView(viewId);
        ImageLoader.with(mContext)
                .url(imageUrl)
                .imageView(view)
                .load();
        return this;
    }

    public BaseViewHolder setImageUrlVisible(@IdRes int viewId, String imageUrl,int width,int height)
    {
        ImageView view = getView(viewId);
        ImageLoader.with(mContext)
                .url(imageUrl)
                .urlWidthHeight(width,height)
                .needCutting()
                .imageView(view)
                .load();
        return this;
    }
    public BaseViewHolder setImageUrlVisibleNeadCut(@IdRes int viewId, String imageUrl,int width,int height)
    {
        ImageView view = getView(viewId);
        ImageLoader.with(mContext)
                .url(imageUrl)
                .urlWidthHeight(width,height)
                .needCutting()
                .imageView(view)
                .load();
        return this;
    }

    public BaseViewHolder setImageUrlVisibleWithPlaceHolder(@IdRes int viewId, String imageUrl,@DrawableRes int resId)
    {

        ImageView view = getView(viewId);
        ImageLoader.with(mContext)
                .url(imageUrl)
                .placeHolder(resId)
                .imageView(view)
                .load();
        return this;
    }

    public BaseViewHolder setImageUrlWithPlaceHolder(@IdRes int viewId, String imageUrl,@DrawableRes int resId)
    {

        ImageView view = getView(viewId);
        if(TextUtils.isEmpty(imageUrl))
        {
            view.setVisibility(View.GONE);
        }else
        {
            view.setVisibility(View.VISIBLE);
            ImageLoader.with(mContext)
                    .url(imageUrl)
                    .placeHolder(resId)
                    .imageView(view)
                    .load();
        }
        return this;
    }




    public BaseViewHolder setCircleImageUrlVisible(@IdRes int viewId, String imageUrl)
    {
        ImageView view = getView(viewId);
        ImageLoader.with(mContext)
                .url(imageUrl)
                .circle()
                .placeHolder(R.mipmap.init_pic)
                .imageView(view)
                .load();
        return this;
    }

    public BaseViewHolder setCircleImageUrlVisibleWithPlaceHolder(@IdRes int viewId, String imageUrl,@DrawableRes int resId)
    {
        ImageView view = getView(viewId);
        ImageLoader.with(mContext)
                .url(imageUrl)
                .circle()
                .placeHolder(resId)
                .imageView(view)
                .load();
        return this;
    }

    public BaseViewHolder setImageUrlWithFirstLetterAsPlaceHolder(@IdRes int viewId, String imageUrl, String title)
    {
        ImageView view = getView(viewId);
        ImageLoader.with(mContext)
                .url(imageUrl)
                .letterWithRandomColor(title)
                .imageView(view)
                .load();
        return this;
    }
}
