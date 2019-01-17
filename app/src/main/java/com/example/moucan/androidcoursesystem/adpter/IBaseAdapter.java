package com.example.moucan.androidcoursesystem.adpter;

import android.view.View;

import java.util.List;

public interface IBaseAdapter <T>{

    void updateAll(List<T> data);
    void addAll(List<T> data);
    void removeItem(int position);
    void insertItem(int position, T data);
    void setOnItemClickListener(OnItemClickListener<T> itemClickListener);
    void setOnItemLongClickListener(OnItemLongClickListener<T> itemLongClickListener);



    //点击事件接口
    interface OnItemClickListener<T> {
        void onItemClick(View view, int position, T data);
    }
    //长按事件接口
    interface OnItemLongClickListener<T> {
        boolean onItemLongClick(View view, int position,T data);
    }
}
