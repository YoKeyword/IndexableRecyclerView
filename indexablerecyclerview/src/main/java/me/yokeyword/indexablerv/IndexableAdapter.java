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
    private IndexableLayout layout;

    public abstract String getIndexName(T data);

    public abstract void setIndexName(T data, String indexName);

    public abstract RecyclerView.ViewHolder onCreateIndexView(ViewGroup parent);

    public abstract RecyclerView.ViewHolder onCreateContentView(ViewGroup parent);

    public abstract void onBindIndexViewHolder(RecyclerView.ViewHolder holder, String indexName);

    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity);

    public void setDatas(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataChanged();
    }

    public void notifyDataChanged() {
        layout.notifyDataChanged();
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
        layout = indexableLayout;
    }
}
