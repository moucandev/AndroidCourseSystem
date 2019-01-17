package com.example.moucan.androidcoursesystem.adpter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unchecked")
class RecyclerGroupImplAdapter extends RecyclerAdapter<RecyclerView.ViewHolder> {

    private int size = 0;
    private ArrayList<ChildManager<?>> childes = new ArrayList<>();
    private SparseArray<WeakReference<RecyclerAdapter<?>>> adapterMap = new SparseArray<>();
    private RecyclerView attached;


    RecyclerGroupImplAdapter() {
        adapterParent = null;
    }


    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerAdapter<?> adapter = adapterMap.get(viewType).get();
        RecyclerView.ViewHolder viewHolder;
        if (adapter instanceof RecyclerGroupImplAdapter) {
            viewHolder = adapter.onCreateViewHolder(parent, viewType);
        } else {
            int itemType = getItemType(viewType);
            viewHolder = adapter.onCreateViewHolder(parent, itemType);
        }
        return viewHolder;
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChildManager childManager = findManager(position);
        int innerPosition = getInnerPosition(position, childManager);
        childManager.child.onBindViewHolder(holder, innerPosition);
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        ChildManager childManager = findManager(position);
        int innerPosition = getInnerPosition(position, childManager);
        childManager.child.onBindViewHolder(holder, innerPosition, payloads);
    }

    private <VH extends RecyclerView.ViewHolder> ChildManager<VH> addManager(RecyclerAdapter<VH> toAdd) {
        ChildManager<VH> childManager = new ChildManager<>(toAdd);
        toAdd.childManager = childManager;
        toAdd.registerAdapterDataObserver(childManager);
        return childManager;
    }

    private <VH extends RecyclerView.ViewHolder> void addAdapter(RecyclerAdapter<VH> toAdd) {
        addAdapter(toAdd, childes.size());
    }

    private <VH extends RecyclerView.ViewHolder> void addAdapter(RecyclerAdapter<VH> toAdd, int position) {
        ChildManager childManager = addManager(toAdd);
        childes.add(position, childManager);
        if (attached != null) {
            toAdd.onAttachedToRecyclerView(attached);
        }
        updateParent(toAdd, this);

    }

    private static void updateParent(RecyclerAdapter toAdd, RecyclerGroupImplAdapter recyclerGroupImplAdapter) {
        toAdd.adapterParent = recyclerGroupImplAdapter;

    }


    @Override
    public final int getItemViewType(int position) {
        ChildManager<?> childManager = findManager(position);
        RecyclerAdapter<?> child = childManager.child;
        int innerPosition = getInnerPosition(position, childManager);
        int itemViewType = childManager.child.getItemViewType(innerPosition);
        Integer integer = child.getPreInt();
        //如果integer 为null 说明这是个GroupAdapter，不用再处理
        if (integer != null) {
            //先处理integer 负数 maxvalue等
            if (itemViewType > 0x7fff || itemViewType < -0x8000) {
                throw new IllegalArgumentException("itemViewType 不能大于 0x7fff或者小于-0x8000");
            }
            if (itemViewType < 0) {
                itemViewType = itemViewType & 0xffff;
            }
            itemViewType = (integer << 16) | itemViewType;
        }
        adapterMap.put(itemViewType, new WeakReference<RecyclerAdapter<?>>(child));
        return itemViewType;
    }

    @Override
    public final void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        for (ChildManager<?> c : childes) {
            c.child.setHasStableIds(hasStableIds);
        }
    }

    private int getInnerPosition(int position, ChildManager childManager) {
        return position - childManager.start;
    }

    final int getAdapterPosition(int position, RecyclerAdapter<?> adapter) {
        int tPosition;
        if (adapterParent != null) {
            tPosition = adapterParent.getAdapterPosition(position, this);
        } else {
            tPosition = position;
        }
        ChildManager childManager = adapter.childManager;
        if (childManager != null && tPosition >= childManager.start && tPosition <= childManager.end) {
            tPosition = tPosition - childManager.start;
        } else {
            return RecyclerView.NO_POSITION;
        }
        return tPosition;
    }

    private ChildManager<?> findManager(int position) {
        for (ChildManager<?> childManager : childes) {
            int end = childManager.end;
            if (position <= end) {
                return childManager;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public final long getItemId(int position) {
        RecyclerAdapter<?> adapter = findManager(position).child;
        return adapter.getItemId(position);
    }


    @Override
    public final void onViewRecycled(RecyclerView.ViewHolder holder) {
        int itemViewType = holder.getItemViewType();
        RecyclerAdapter adapter = adapterMap.get(itemViewType).get();
        adapter.onViewRecycled(holder);
    }

    @Override
    public final boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        int itemViewType = holder.getItemViewType();
        RecyclerAdapter adapter = adapterMap.get(itemViewType).get();
        return adapter.onFailedToRecycleView(holder);
    }

    @Override
    public final void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        int itemViewType = holder.getItemViewType();
        RecyclerAdapter adapter = adapterMap.get(itemViewType).get();
        adapter.onViewAttachedToWindow(holder);
    }

    @Override
    public final void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        int itemViewType = holder.getItemViewType();
        RecyclerAdapter adapter = adapterMap.get(itemViewType).get();
        adapter.onViewDetachedFromWindow(holder);
    }


    @Override
    public final void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.attached = recyclerView;
        for (ChildManager<?> childManager : childes) {
            childManager.child.onAttachedToRecyclerView(recyclerView);
        }
    }

    @Override
    public final void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        this.attached = null;
        for (ChildManager<?> childManager : childes) {
            childManager.child.onDetachedFromRecyclerView(recyclerView);
        }
    }

    public RecyclerAdapter<?> getAdapterAt(int adapterPosition) {
        return childes.get(adapterPosition).child;
    }

    public int getAdapterCount() {
        return childes.size();
    }

    @Override
    public final int getItemCount() {
        return size;
    }

    public final int getIndexOf(RecyclerAdapter<?> adapter) {
        RecyclerAdapter child = adapter;
        while (child.adapterParent != null && child.adapterParent != this) {
            child = child.adapterParent;
        }
        if(child.adapterParent==null){
            return -1;
        }else{
            return child.childManager.adapterPosition;
        }
    }
    public final @Nullable RecyclerAdapter<?> findPositionAdapter(int position){
        for (ChildManager<?> childe : childes) {
            if(childe.start<=position&&childe.end>=position){
                return childe.child;
            }
        }
        return null;
    }
    public final @Nullable int findPositionAdapterPosition(int position){
        for (ChildManager<?> childe : childes) {
            if(childe.start<=position&&childe.end>=position){
                return childe.adapterPosition;
            }
        }
        return -1;
    }



    void add(RecyclerAdapter<?> bean) {
        int size = childes.size();
        add(bean, size);
    }

    void add(RecyclerAdapter<?> item, int position) {
        addAdapter(item, position);
        notifyAdapterInserted(position, 1);
    }


    void addAll(List<? extends RecyclerAdapter<?>> data) {
        int size = childes.size();
        for (RecyclerAdapter<?> recyclerAdapter : data) {
            addAdapter(recyclerAdapter);
        }
        notifyAdapterInserted(size, data.size());
    }

    void addAllAt(List<? extends RecyclerAdapter<?>> data, int position) {
        int size = childes.size();
        for (int i = 0; i < data.size(); i++) {
            RecyclerAdapter<?> recyclerAdapter = data.get(i);
            addAdapter(recyclerAdapter, position + i);
        }
        notifyAdapterInserted(position, data.size());
    }

    int remove(RecyclerAdapter<?> recyclerAdapter) {
        int index = childes.indexOf(recyclerAdapter.childManager);
        if (index >= 0) {
            remove(index);
        }
        return index;
    }

    RecyclerAdapter<?> remove(int position) {
        ChildManager<?> remove = childes.remove(position);
        if (remove != null) {
            removeChild(remove);
            notifyAdapterRemoved(position, 1);
            return remove.child;
        }
        return null;
    }

    void removeAll() {
        int size = childes.size();
        for (ChildManager<?> childManager : childes) {
            removeChild(childManager);
        }
        childes.clear();
        notifyAdapterRemoved(0, size);
    }

    private void removeChild(ChildManager childManager) {
        childManager.child.unregisterAdapterDataObserver(childManager);
        if (attached != null) {
            childManager.child.onDetachedFromRecyclerView(attached);
        }
        updateParent(childManager.child, null);
    }


    void update(List<RecyclerAdapter<?>> newData) {
        childes.clear();
        for (RecyclerAdapter<?> recyclerAdapter : newData) {
            addAdapter(recyclerAdapter);
        }
        notifyChanged();
    }

    void update(RecyclerAdapter<?> item, int position) {
        ChildManager<?> vhChildManager = childes.get(position);
        updateParent(vhChildManager.child, null);
        ChildManager<?> vhChildManager1 = new ChildManager<>(item);
        childes.set(position, vhChildManager1);
        notifyAdapterChanged(position);
    }

    private void notifyAdapterInserted(int adapterPosition, int adapterCount) {
        if (adapterCount <= 0) {
            return;
        }
        int totalCount;
        totalCount = updateManagers(adapterPosition, adapterPosition + adapterCount - 1);
        if (totalCount > 0) {
            updateManagers(adapterPosition + adapterCount, childes.size() - 1);
            size += totalCount;
            int start = getNewStart(adapterPosition);
            notifyItemRangeInserted(start, totalCount);
        }
    }


    private void notifyAdapterRemoved(int adapterPosition, int adapterCount) {
        if (adapterCount <= 0) {
            return;
        }
        int totalCount;
        if (childes.size() != 0) {
            updateManagers(adapterPosition, childes.size() - 1);
            ChildManager childManager = childes.get(childes.size() - 1);
            int newSize = childManager.end + 1;
            totalCount = size - newSize;
            if (totalCount <= 0) {
                return;
            }
            size = newSize;
            int start = getNewStart(adapterPosition);
            notifyItemRangeRemoved(start, totalCount);
        } else {
            notifyItemRangeRemoved(0, size);
            size = 0;
        }
    }

    private void notifyAdapterMoved(int adapterFromPosition, int adapterToPosition, int adapterCount) {
        if (adapterCount <= 0 || adapterFromPosition == adapterToPosition) {
            return;
        }
        int indexOfMovedFirst;
        int startPosition;
        int endPosition;
        if (adapterFromPosition < adapterToPosition) {
            indexOfMovedFirst = adapterToPosition - adapterCount + 1;
            startPosition = adapterFromPosition;
            endPosition = adapterToPosition;
        } else {
            indexOfMovedFirst = adapterToPosition;
            startPosition = adapterToPosition;
            endPosition = adapterFromPosition + adapterCount - 1;
        }
        int totalCount = 0;
        for (int i = 0; i < adapterCount; i++) {
            RecyclerAdapter adapter = childes.get(i + indexOfMovedFirst).child;
            totalCount += adapter.getItemCount();
        }
        int fromStart;
        int toStart;
        ChildManager<?> childManager = childes.get(indexOfMovedFirst);
        //start位置就存储在更新位置之前的manager中
        fromStart = childManager.start;
        updateManagers(startPosition, endPosition);
        toStart = childManager.start;
        if (totalCount > 0) {
            for (int i = 0; i < totalCount; i++) {
                notifyItemMoved(fromStart + i, toStart + i);
            }
        }
    }

    private void notifyAdapterChanged(int adapterPosition) {
        updateManagers(adapterPosition, childes.size() - 1);
        ChildManager<?> childManager = childes.get(childes.size() - 1);
        size = childManager.end + 1;
        notifyDataSetChanged();

    }

    private void notifyChanged() {
        if (childes.size() == 0) {
            size = 0;
        } else {
            updateManagers(0, childes.size() - 1);
            ChildManager<?> childManager = childes.get(childes.size() - 1);
            size = childManager.end + 1;
        }
        notifyDataSetChanged();

    }


    private void notifyAdapterContentInserted(int adapterPosition, int innerPosition, int count) {
        //先更新group
        if (count <= 0) {
            return;
        }
        size += count;
        ChildManager<?> childManager = childes.get(adapterPosition);
        updateManagers(adapterPosition, childes.size() - 1);
        notifyItemRangeInserted(childManager.start + innerPosition, count);
    }

    private void notifyAdapterContentChanged(int adapterPosition, int innerPosition, int count, Object payload) {
        if (count <= 0) {
            return;
        }
        ChildManager childManager = childes.get(adapterPosition);
        notifyItemRangeChanged(childManager.start + innerPosition, count, payload);
    }

    private void notifyAdapterContentChanged(int adapterPosition, int innerPosition, int count) {
        if (count <= 0) {
            return;
        }
        ChildManager childManager = childes.get(adapterPosition);
        notifyItemRangeChanged(childManager.start + innerPosition, count);
    }

    private void notifyAdapterContentRemoved(int adapterPosition, int innerPosition, int count) {
        if (count <= 0) {
            return;
        }
        size -= count;
        ChildManager childManager = childes.get(adapterPosition);
        //更新坐标
        updateManagers(adapterPosition, childes.size() - 1);
        //通知父Observer
        notifyItemRangeRemoved(childManager.start + innerPosition, count);

    }

    private void notifyAdapterContentMoved(int adapterPosition, int fromPosition, int toPosition, int itemCount) {
        ChildManager childManager = childes.get(adapterPosition);
        for (int i = 0; i < itemCount; i++) {
            notifyItemMoved(childManager.start + fromPosition + i, childManager.start + toPosition + i);
        }
    }

    private int updateManager(int nowStart, int adapterPosition) {
        ChildManager childManager = childes.get(adapterPosition);
        int itemCount = childManager.child.getItemCount();
        childManager.start = nowStart;
        childManager.end = nowStart + itemCount - 1;
        childManager.adapterPosition = adapterPosition;
        return itemCount;
    }

    private int updateManagers(int startPosition, int endPosition) {
        final int start;
        int totalCount = 0;
        start = getNewStart(startPosition);
        if (startPosition < 0) {
            startPosition = 0;
        }
        int childCount = childes.size();
        if (endPosition >= childCount) {
            endPosition = childCount - 1;
        }
        int nowStart;
        for (int i = startPosition; i <= endPosition; i++) {
            nowStart = start + totalCount;
            int itemCount = updateManager(nowStart, i);
            totalCount += itemCount;
        }
        return totalCount;
    }

    private int getNewStart(int startPosition) {
        int start;
        if (startPosition > 0) {
            ChildManager lastM = childes.get(startPosition - 1);
            start = lastM.end + 1;
        } else {
            start = 0;
        }
        return start;
    }

    public <H extends RecyclerView.ViewHolder> int getRealPosition(int position, RecyclerAdapter<H> hRecyclerAdapter) {
        position = hRecyclerAdapter.childManager.start + position;
        if (adapterParent != null) {
            position = adapterParent.getRealPosition(position, this);
        }
        return position;
    }

    static class ChildManager<VH extends RecyclerView.ViewHolder> extends RecyclerView.AdapterDataObserver {
        private int start;
        private int end;
        private int adapterPosition;
        private final RecyclerAdapter<VH> child;

        ChildManager(RecyclerAdapter<VH> child) {
            super();
            this.child = child;
        }

        @Override
        public final void onChanged() {
            child.adapterParent.notifyAdapterChanged(adapterPosition);
        }

        @Override
        public final void onItemRangeChanged(int positionStart, int itemCount) {
            child.adapterParent.notifyAdapterContentChanged(adapterPosition, positionStart, itemCount);
        }

        @Override
        public final void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            child.adapterParent.notifyAdapterContentChanged(adapterPosition, positionStart, itemCount, payload);
        }

        @Override
        public final void onItemRangeInserted(int positionStart, int itemCount) {
            child.adapterParent.notifyAdapterContentInserted(adapterPosition, positionStart, itemCount);
        }

        @Override
        public final void onItemRangeRemoved(int positionStart, int itemCount) {
            child.adapterParent.notifyAdapterContentRemoved(adapterPosition, positionStart, itemCount);
        }

        @Override
        public final void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            child.adapterParent.notifyAdapterContentMoved(adapterPosition, fromPosition, toPosition, itemCount);
        }


    }

}
