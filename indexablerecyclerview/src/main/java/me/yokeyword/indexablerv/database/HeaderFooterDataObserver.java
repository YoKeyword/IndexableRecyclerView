package me.yokeyword.indexablerv.database;

/**
 * Created by YoKey on 16/10/13.
 */
public class HeaderFooterDataObserver<T> {
    /**
     * This method is called when the entire data set has changed,
     * refresh UI
     */
    public void onChanged() {
        // Do nothing
    }

    public void onAdd(T preData, T data) {
        // Do nothing
    }

    public void onRemove(T object) {
        // Do nothing
    }
//
//    public void onAddAll(int itemType, int position, Collection<T> datas) {
//        // Do nothing
//    }
//
//    public void onRemoveAll(int itemType, Collection<T> datas) {
//        // Do nothing
//    }
}
