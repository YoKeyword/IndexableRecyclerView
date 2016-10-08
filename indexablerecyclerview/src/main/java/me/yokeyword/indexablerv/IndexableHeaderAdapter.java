package me.yokeyword.indexablerv;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoKey on 16/10/8.
 */
public abstract class IndexableHeaderAdapter<T> {
    private ArrayList<EntityWrapper<T>> mEntityWrapperList = new ArrayList<>();
    private OnItemHeaderClickListener<T> mListener;

    /**
     * none indexTitle
     */
    public IndexableHeaderAdapter(String index, List<T> datas) {
        this(index, null, datas);
    }

    public IndexableHeaderAdapter(String index, String indexTitle, List<T> datas) {
        if (indexTitle != null) {
            EntityWrapper<T> wrapper = new EntityWrapper<>();
            wrapper.setIndex(index);
            wrapper.setIndexTitle(indexTitle);
            wrapper.setItemType(EntityWrapper.TYPE_INDEX);
            mEntityWrapperList.add(wrapper);
        }
        for (int i = 0; i < datas.size(); i++) {
            EntityWrapper<T> wrapper = new EntityWrapper<>();
            wrapper.setIndex(index);
            if (indexTitle == null && i != 0) {
                indexTitle = "";
            }
            wrapper.setIndexTitle(indexTitle);
            wrapper.setData(datas.get(i));
            mEntityWrapperList.add(wrapper);
        }
    }

    public abstract int getItemViewType();

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);

    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity);

    /**
     * set Content-ItemView click listener
     */
    public void setOnItemHeaderClickListener(OnItemHeaderClickListener<T> listener) {
        this.mListener = listener;
    }

    OnItemHeaderClickListener<T> getOnItemHeaderClickListener() {
        return mListener;
    }

    ArrayList<EntityWrapper<T>> getDatas() {
        for (EntityWrapper<T> wrapper : mEntityWrapperList) {
            if (wrapper.getItemType() == EntityWrapper.TYPE_CONTENT) {
                wrapper.setItemType(getItemViewType());
            }
        }
        return mEntityWrapperList;
    }

    public interface OnItemHeaderClickListener<T> {
        void onItemClick(View v, int currentPosition, T entity);
    }
}
