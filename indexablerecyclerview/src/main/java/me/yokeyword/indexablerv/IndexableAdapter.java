package me.yokeyword.indexablerv;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoKey on 16/10/6.
 */
public abstract class IndexableAdapter<T> {
    private List<T> mDatas = new ArrayList<>();
    private IndexableLayout mLayout;

    private OnItemIndexClickListener mIndexClickListener;
    private OnItemContentClickListener mContentClickListener;

    public abstract String getIndexField(T data);

    public abstract void setIndexField(T data, String indexField);

    public abstract RecyclerView.ViewHolder onCreateIndexViewHolder(ViewGroup parent);

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);

    public abstract void onBindIndexViewHolder(RecyclerView.ViewHolder holder, String indexTitle);

    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity);

    public void setDatas(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        if (mLayout != null) {
            notifyDataChanged();
        }
    }

    /**
     * set Index-ItemView click listener
     */
    public void setOnItemIndexClickListener(OnItemIndexClickListener listener) {
        if (mLayout != null) {
            mLayout.setOnItemIndexClickListener(listener);
        } else {
            this.mIndexClickListener = listener;
        }
    }

    /**
     * set Content-ItemView click listener
     */
    public <T> void setOnItemContentClickListener(OnItemContentClickListener<T> listener) {
        if (mLayout != null) {
            mLayout.setOnItemContentClickListener(listener);
        } else {
            this.mContentClickListener = listener;
        }
    }

    public void notifyDataChanged() {
        mLayout.notifyDataChanged();
    }

    int getItemCount() {
        return mDatas.size();
    }

    List<T> getItems() {
        return mDatas;
    }

    T getItem(int position) {
        return mDatas.size() > position ? mDatas.get(position) : null;
    }

    void setLayout(IndexableLayout indexableLayout) {
        mLayout = indexableLayout;
        if (mDatas.size() > 0) {
            notifyDataChanged();
        }
        if(mIndexClickListener!=null){
            mLayout.setOnItemIndexClickListener(mIndexClickListener);
        }
        if(mContentClickListener!=null){
            mLayout.setOnItemContentClickListener(mContentClickListener);
        }
    }

    public interface OnItemIndexClickListener {
        void onItemClick(View v, int currentPosition, String indexName);
    }

    public interface OnItemContentClickListener<T> {
        void onItemClick(View v, int originalPosition, int currentPosition, T entity);
    }
}
