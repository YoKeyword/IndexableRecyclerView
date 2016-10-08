package me.yokeyword.indexablerv;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoKey on 16/10/6.
 */
public abstract class IndexableAdapter<T> {
    private List<T> mDatas = new ArrayList<>();
    private IndexableLayout mLayout;

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
    }
}
