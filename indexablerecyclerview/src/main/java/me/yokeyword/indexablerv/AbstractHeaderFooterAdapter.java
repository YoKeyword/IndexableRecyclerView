package me.yokeyword.indexablerv;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.indexablerv.database.HeaderFooterDataObservable;
import me.yokeyword.indexablerv.database.HeaderFooterDataObserver;
import me.yokeyword.indexablerv.database.IndexBarDataObservable;
import me.yokeyword.indexablerv.database.IndexBarDataObserver;

/**
 * Created by YoKey on 16/10/16.
 */

abstract class AbstractHeaderFooterAdapter<T> {
    private final HeaderFooterDataObservable mDataSetObservable = new HeaderFooterDataObservable();
    private final IndexBarDataObservable mIndexBarDataSetObservable = new IndexBarDataObservable();

    ArrayList<EntityWrapper<T>> mEntityWrapperList = new ArrayList<>();
    protected OnItemClickListener<T> mListener;
    protected OnItemLongClickListener<T> mLongListener;

    private String mIndex, mIndexTitle;

    /**
     * 不想显示哪个就传null
     *
     * @param index      IndexBar的字母索引
     * @param indexTitle IndexTitle
     * @param datas      数据源
     */
    public AbstractHeaderFooterAdapter(String index, String indexTitle, List<T> datas) {
        this.mIndex = index;
        this.mIndexTitle = indexTitle;

        if (indexTitle != null) {
            EntityWrapper<T> wrapper = wrapEntity();
            wrapper.setItemType(EntityWrapper.TYPE_TITLE);
        }
        for (int i = 0; i < datas.size(); i++) {
            EntityWrapper<T> wrapper = wrapEntity();
            wrapper.setData(datas.get(i));
        }
    }

    private EntityWrapper<T> wrapEntity() {
        EntityWrapper<T> wrapper = new EntityWrapper<>();
        wrapper.setIndex(mIndex);
        wrapper.setIndexTitle(mIndexTitle);
        wrapper.setHeaderFooterType(getHeaderFooterType());
        mEntityWrapperList.add(wrapper);
        return wrapper;
    }

    private EntityWrapper<T> wrapEntity(int pos) {
        EntityWrapper<T> wrapper = new EntityWrapper<>();
        wrapper.setIndex(mIndex);
        wrapper.setIndexTitle(mIndexTitle);
        wrapper.setHeaderFooterType(getHeaderFooterType());
        mEntityWrapperList.add(pos, wrapper);
        return wrapper;
    }

    public abstract int getItemViewType();

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);

    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity);

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    public void addData(T data) {
        int size = mEntityWrapperList.size();

        EntityWrapper<T> wrapper = wrapEntity();
        wrapper.setItemType(getItemViewType());
        wrapper.setData(data);

        if (size > 0) {
            mDataSetObservable.notifyAdd(getHeaderFooterType() == EntityWrapper.TYPE_HEADER, mEntityWrapperList.get(size - 1), wrapper);
            mIndexBarDataSetObservable.notifyChanged();
        }
    }

    public void removeData(T data) {
        for (EntityWrapper wrapper : mEntityWrapperList) {
            if (wrapper.getData() == data) {
                mEntityWrapperList.remove(wrapper);
                mDataSetObservable.notifyRemove(getHeaderFooterType() == EntityWrapper.TYPE_HEADER, wrapper);
                mIndexBarDataSetObservable.notifyChanged();
                return;
            }
        }
    }

    int getHeaderFooterType() {
        return EntityWrapper.TYPE_HEADER;
    }

    public void addData(int position, T data) {
        int size = mEntityWrapperList.size();
        if (position >= size) {
            return;
        }

        EntityWrapper<T> wrapper = wrapEntity(position + 1);
        wrapper.setItemType(getItemViewType());
        wrapper.setData(data);

        if (size > 0) {
            mDataSetObservable.notifyAdd(getHeaderFooterType() == EntityWrapper.TYPE_HEADER, mEntityWrapperList.get(position), wrapper);
            mIndexBarDataSetObservable.notifyChanged();
        }
    }

    public void addDatas(List<T> datas) {
        for (int i = 0; i < datas.size(); i++) {
            addData(datas.get(i));
        }
    }

    public void addDatas(int position, List<T> datas) {
        int size = mEntityWrapperList.size();
        if (position >= size) {
            return;
        }

        for (int i = datas.size() - 1; i >= 0; i--) {
            addData(position, datas.get(i));
        }
    }

//    public void removeAll(List<T> datas) {
//        // TODO: 16/10/27
//    }

    OnItemClickListener<T> getOnItemClickListener() {
        return mListener;
    }


    OnItemLongClickListener getOnItemLongClickListener() {
        return mLongListener;
    }

    ArrayList<EntityWrapper<T>> getDatas() {
        for (EntityWrapper<T> wrapper : mEntityWrapperList) {
            if (wrapper.getItemType() == EntityWrapper.TYPE_CONTENT) {
                wrapper.setItemType(getItemViewType());
            }
        }
        return mEntityWrapperList;
    }

    void registerDataSetObserver(HeaderFooterDataObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    void unregisterDataSetObserver(HeaderFooterDataObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    void registerIndexBarDataSetObserver(IndexBarDataObserver observer) {
        mIndexBarDataSetObservable.registerObserver(observer);
    }

    void unregisterIndexBarDataSetObserver(IndexBarDataObserver observer) {
        mIndexBarDataSetObservable.unregisterObserver(observer);
    }

    interface OnItemClickListener<T> {
        void onItemClick(View v, int currentPosition, T entity);
    }

    interface OnItemLongClickListener<T> {
        boolean onItemLongClick(View v, int currentPosition, T entity);
    }
}
