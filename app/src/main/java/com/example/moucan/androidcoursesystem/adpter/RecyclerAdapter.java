package com.example.moucan.androidcoursesystem.adpter;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A simple {@link RecyclerView.Adapter} subclass.
 * Created by summer on 16/9/29.
 */

public abstract class RecyclerAdapter<H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {
    private final Integer preInt;
    RecyclerGroupImplAdapter adapterParent;
    RecyclerGroupImplAdapter.ChildManager<H> childManager;
    private static final Map<Class, Integer> recyclerClassMap = new ConcurrentHashMap<>();
    private static AtomicInteger atomicInteger = new AtomicInteger(1);

    public RecyclerAdapter() {
        Class<? extends RecyclerAdapter> aClass = this.getClass();
        if (RecyclerGroupImplAdapter.class.isAssignableFrom(aClass)) {
            this.preInt = null;
            return;
        }
        Integer preInt = recyclerClassMap.get(aClass);
        if (preInt == null) {
            preInt = atomicInteger.getAndIncrement();
            recyclerClassMap.put(aClass, preInt);
        }
        this.preInt = preInt;
    }

    public int getPosition(int holderAdapterPosition) {
        if (adapterParent != null) {
            return adapterParent.getAdapterPosition(holderAdapterPosition, this);
        } else {
            return holderAdapterPosition;
        }
    }
    public int getRealPosition(int position) {
        if (adapterParent != null) {
            return adapterParent.getRealPosition(position, this);
        } else {
            return position;
        }
    }

    public static int getItemType(int holderViewType) {
        int itemType = holderViewType & 0xffff;
        if (itemType > 0x7fff) {
            itemType = itemType | 0xffff0000;
        }
        return itemType;
    }


    final Integer getPreInt() {
        return preInt;
    }
}

