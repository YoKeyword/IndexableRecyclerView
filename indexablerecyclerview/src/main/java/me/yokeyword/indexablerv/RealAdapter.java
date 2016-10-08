package me.yokeyword.indexablerv;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by YoKey on 16/10/6.
 */
class RealAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<EntityWrapper<T>> mDatasList = new ArrayList<>();
    private IndexableAdapter<T> mAdapter;

    private IndexableLayout.OnItemIndexClickListener mIndexClickListener;
    private IndexableLayout.OnItemContentClickListener<T> mContentClickListener;

    void setIndexableAdapter(IndexableAdapter<T> adapter) {
        this.mAdapter = adapter;
    }

    void setDatas(ArrayList<EntityWrapper<T>> datas) {
        this.mDatasList = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mDatasList.get(position).getItemType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        final RecyclerView.ViewHolder holder = viewType == EntityWrapper.TYPE_INDEX ?
                mAdapter.onCreateIndexView(parent) : mAdapter.onCreateContentView(parent);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                EntityWrapper<T> wrapper = mDatasList.get(position);
                if (viewType == EntityWrapper.TYPE_INDEX && mIndexClickListener != null) {
                    mIndexClickListener.onItemClick(v, position, wrapper.getIndex());
                } else if (viewType == EntityWrapper.TYPE_CONTENT && mContentClickListener != null) {
                    mContentClickListener.onItemClick(v, wrapper.getOriginalPosition(), position, wrapper.getData());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EntityWrapper<T> item = mDatasList.get(position);
        if (getItemViewType(position) == EntityWrapper.TYPE_INDEX) {
            mAdapter.onBindIndexViewHolder(holder, item.getIndex());
        } else {
            mAdapter.onBindContentViewHolder(holder, item.getData());
        }
    }

    @Override
    public int getItemCount() {
        return mDatasList.size();
    }

    void setOnItemIndexClickListener(IndexableLayout.OnItemIndexClickListener listener) {
        this.mIndexClickListener = listener;
    }

    void setOnItemContentClickListener(IndexableLayout.OnItemContentClickListener<T> listener) {
        this.mContentClickListener = listener;
    }
}
