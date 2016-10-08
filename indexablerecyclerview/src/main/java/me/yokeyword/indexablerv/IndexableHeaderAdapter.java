package me.yokeyword.indexablerv;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoKey on 16/10/8.
 */
public abstract class IndexableHeaderAdapter<T> {
    private ArrayList<EntityWrapper<T>> mEntityWrapperList = new ArrayList<>();

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
        for (T data : datas) {
            EntityWrapper<T> wrapper = new EntityWrapper<>();
            wrapper.setIndex(index);
            wrapper.setIndexTitle(indexTitle);
            wrapper.setData(data);
            mEntityWrapperList.add(wrapper);
        }
    }

    public abstract int getItemViewType();

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);

    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity);

    ArrayList<EntityWrapper<T>> getDatas() {
        for (EntityWrapper<T> wrapper : mEntityWrapperList) {
            if (wrapper.getItemType() == EntityWrapper.TYPE_CONTENT) {
                wrapper.setItemType(getItemViewType());
            }
        }
        return mEntityWrapperList;
    }
}
