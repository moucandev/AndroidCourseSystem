package com.example.moucan.androidcoursesystem.adpter;

import android.view.ViewGroup;

import java.util.List;

public abstract class DifItemBaseAdapter<T> extends BaseAdapter<T>{
    public DifItemBaseAdapter(List<T> datas)

    {
        this.mDatas.addAll(datas);
    }
    public DifItemBaseAdapter(){}

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDatas.get(position));
    }

    @Override
    public BaseViewHolder createViewHolderInternal(ViewGroup parent, int viewType) {
        int layoutId = getLayoutId(viewType);
        BaseViewHolder holder = BaseViewHolder.creatViewHolder(parent.getContext(), layoutId, parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        viewBindData(holder, mDatas, position);
    }


    public abstract void viewBindData(BaseViewHolder holder, final List<T> datas, final int position);

    public abstract int getItemViewType(int position, T t);

    public abstract int getLayoutId(int itemType);
}
