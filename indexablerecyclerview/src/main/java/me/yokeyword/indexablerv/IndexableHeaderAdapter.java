package me.yokeyword.indexablerv;

import java.util.List;

/**
 * HeaderView Adapter
 * Created by YoKey on 16/10/8.
 */
public abstract class IndexableHeaderAdapter<T> extends AbstractHeaderFooterAdapter<T>{

    public IndexableHeaderAdapter(String index, String indexTitle, List<T> datas) {
        super(index, indexTitle, datas);
    }

    @Override
    int getHeaderFooterType() {
        return EntityWrapper.TYPE_HEADER;
    }

    /**
     * set Content-ItemView click listener
     */
    public void setOnItemHeaderClickListener(OnItemHeaderClickListener<T> listener) {
        this.mListener = listener;
    }

    /**
     * set Content-ItemView longClick listener
     */
    public void setOnItemHeaderLongClickListener(OnItemHeaderLongClickListener<T> listener) {
        this.mLongListener = listener;
    }

    public interface OnItemHeaderClickListener<T> extends OnItemClickListener<T>{
    }

    public interface OnItemHeaderLongClickListener<T> extends OnItemLongClickListener<T>{
    }
}
