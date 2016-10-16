package me.yokeyword.indexablerv;

import java.util.List;

/**
 * FooterView Adapter
 * Created by YoKey on 16/10/14.
 */
public abstract class IndexableFooterAdapter<T> extends AbstractHeaderFooterAdapter<T> {

    public IndexableFooterAdapter(String index, String indexTitle, List<T> datas) {
        super(index, indexTitle, datas);

        for (EntityWrapper wrapper : mEntityWrapperList) {
            wrapper.setHeaderFooterType(EntityWrapper.TYPE_FOOTER);
        }
    }
}
