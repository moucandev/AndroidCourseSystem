package com.example.moucan.androidcoursesystem;

import android.view.View;
import android.view.ViewGroup;

import com.example.moucan.androidcoursesystem.utils.CollectionUtils;
import com.example.moucan.androidcoursesystem.view.IBaseAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public abstract class BaseAdapter<T> extends RecyclerAdapter<BaseViewHolder> implements IBaseAdapter<T> {
    protected List<T> mDatas = new ArrayList<>();

    private OnItemClickListener<T> mOnItemClickListener;
    private OnItemLongClickListener<T> mOnItemLongClickListener;


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = createViewHolderInternal(parent, viewType);
        if (viewHolder != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener == null) {
                        return;
                    }
                    int position = getPosition(viewHolder.getAdapterPosition());
                    T t = mDatas.get(position);
                    mOnItemClickListener.onItemClick(v, position, t);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemLongClickListener == null) {
                        return false;
                    }
                    int position = getPosition(viewHolder.getAdapterPosition());
                    T t = mDatas.get(position);
                    return mOnItemLongClickListener.onItemLongClick(v, position, t);
                }
            });
        }
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /**
     * ------------------------------    常用公共方法    ------------------------------------
     */


    @Override
    public void setOnItemClickListener(OnItemClickListener<T> itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener<T> itemLongClickListener) {
        mOnItemLongClickListener = itemLongClickListener;
    }


    public List<T> getData() {
        return mDatas;
    }

    public void setData(List<T> data) {
        this.mDatas = data;
    }

    @Override
    public synchronized void updateAll(List<T> data) {
        mDatas.clear();
        if (data == null) {
            return;
        }
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(List<T> data) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        int toAdd = data.size();
        int size = mDatas.size();
        mDatas.addAll(data);
        notifyItemRangeInserted(size, toAdd);
    }

    @Override
    public void removeItem(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void insertItem(int position, T data) {
        mDatas.add(position, data);
        notifyItemInserted(position);
    }

    public void clearAll() {
        mDatas = new ArrayList<>();
        notifyDataSetChanged();
    }

    public abstract BaseViewHolder createViewHolderInternal(@NonNull ViewGroup parent, int viewType);


}

