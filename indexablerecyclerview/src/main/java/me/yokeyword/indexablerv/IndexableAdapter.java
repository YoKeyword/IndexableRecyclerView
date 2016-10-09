package me.yokeyword.indexablerv;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoKey on 16/10/6.
 */
public abstract class IndexableAdapter<T extends IndexableEntity> {
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    private List<T> mDatas = new ArrayList<>();

    private OnItemTitleClickListener mTitleClickListener;
    private OnItemContentClickListener mContentClickListener;
    private OnItemTitleLongClickListener mTitleLongClickListener;
    private OnItemContentLongClickListener mContentLongClickListener;

    public abstract RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent);

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);

    public abstract void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle);

    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity);

    public void setDatas(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * set Index-ItemView click listener
     */
    public void setOnItemTitleClickListener(OnItemTitleClickListener listener) {
        this.mTitleClickListener = listener;
        notifySetListener();
    }

    /**
     * set Content-ItemView click listener
     */
    public void setOnItemContentClickListener(OnItemContentClickListener<T> listener) {
        this.mContentClickListener = listener;
        notifySetListener();
    }

    /**
     * set Index-ItemView longClick listener
     */
    public void setOnItemTitleLongClickListener(OnItemTitleLongClickListener listener) {
        this.mTitleLongClickListener = listener;
        notifySetListener();
    }

    /**
     * set Content-ItemView longClick listener
     */
    public void setOnItemContentLongClickListener(OnItemContentLongClickListener<T> listener) {
        this.mContentLongClickListener = listener;
        notifySetListener();
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    private void notifySetListener() {
        // set listeners
        mDataSetObservable.notifyInvalidated();
    }

    List<T> getItems() {
        return mDatas;
    }

    OnItemTitleClickListener getOnItemTitleClickListener() {
        return mTitleClickListener;
    }

    OnItemTitleLongClickListener getOnItemTitleLongClickListener() {
        return mTitleLongClickListener;
    }

    OnItemContentClickListener getOnItemContentClickListener() {
        return mContentClickListener;
    }

    OnItemContentLongClickListener getOnItemContentLongClickListener() {
        return mContentLongClickListener;
    }

    void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public interface OnItemTitleClickListener {
        void onItemClick(View v, int currentPosition, String indexTitle);
    }

    public interface OnItemContentClickListener<T> {
        void onItemClick(View v, int originalPosition, int currentPosition, T entity);
    }

    public interface OnItemTitleLongClickListener {
        boolean onItemLongClick(View v, int currentPosition, String indexTitle);
    }

    public interface OnItemContentLongClickListener<T> {
        boolean onItemLongClick(View v, int originalPosition, int currentPosition, T entity);
    }
}
