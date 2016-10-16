package me.yokeyword.indexablerv;

import java.util.List;

/**
 * HeaderView Adapter
 * Created by YoKey on 16/10/8.
 */
public abstract class IndexableHeaderAdapter<T> extends AbstractHeaderFooterAdapter<T>{

    public IndexableHeaderAdapter(String index, String indexTitle, List<T> datas) {
        super(index, indexTitle, datas);

        for (EntityWrapper wrapper : mEntityWrapperList) {
            wrapper.setHeaderFooterType(EntityWrapper.TYPE_HEADER);
        }
    }
}
