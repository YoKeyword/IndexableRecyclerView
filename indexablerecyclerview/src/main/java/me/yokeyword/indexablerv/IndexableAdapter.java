package me.yokeyword.indexablerv;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.yokeyword.indexablerv.database.DataObservable;
import me.yokeyword.indexablerv.database.DataObserver;

/**
 * Created by YoKey on 16/10/6.
 */
public abstract class IndexableAdapter<T extends IndexableEntity> {
    static final int TYPE_ALL = 0;
    static final int TYPE_CLICK_TITLE = 1;
    static final int TYPE_CLICK_CONTENT = 2;
    static final int TYPE_LONG_CLICK_TITLE = 3;
    static final int TYPE_LONG_CLICK_CONTENT = 4;
    private final DataObservable mDataSetObservable = new DataObservable();

    private List<T> mDatas;

    private IndexCallback<T> mCallback;
    private OnItemTitleClickListener mTitleClickListener;
    private OnItemContentClickListener mContentClickListener;
    private OnItemTitleLongClickListener mTitleLongClickListener;
    private OnItemContentLongClickListener mContentLongClickListener;

    public abstract RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent);

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);

    public abstract void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle);

    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity);

    public void setDatas(List<T> datas) {
        setDatas(datas, null);
    }

    /**
     * @param callback Register a callback to be invoked when this datas is processed.
     */
    public void setDatas(List<T> datas, IndexCallback<T> callback) {
        this.mCallback = callback;
        mDatas = datas;
        notifyInited();
    }

    /**
     * set Index-ItemView click listener
     */
    public void setOnItemTitleClickListener(OnItemTitleClickListener listener) {
        this.mTitleClickListener = listener;
        notifySetListener(TYPE_CLICK_TITLE);
    }

    /**
     * set Content-ItemView click listener
     */
    public void setOnItemContentClickListener(OnItemContentClickListener<T> listener) {
        this.mContentClickListener = listener;
        notifySetListener(TYPE_CLICK_CONTENT);
    }

    /**
     * set Index-ItemView longClick listener
     */
    public void setOnItemTitleLongClickListener(OnItemTitleLongClickListener listener) {
        this.mTitleLongClickListener = listener;
        notifySetListener(TYPE_LONG_CLICK_TITLE);
    }

    /**
     * set Content-ItemView longClick listener
     */
    public void setOnItemContentLongClickListener(OnItemContentLongClickListener<T> listener) {
        this.mContentLongClickListener = listener;
        notifySetListener(TYPE_LONG_CLICK_CONTENT);
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyInited();
//        mDataSetObservable.notifyChanged();
    }

    private void notifyInited() {
        mDataSetObservable.notifyInited();
    }

    private void notifySetListener(int type) {
        mDataSetObservable.notifySetListener(type);
    }

    public List<T> getItems() {
        return mDatas;
    }

    IndexCallback<T> getIndexCallback() {
        return mCallback;
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

    void registerDataSetObserver(DataObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    void unregisterDataSetObserver(DataObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public interface IndexCallback<T> {
        void onFinished(List<EntityWrapper<T>> datas);
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
