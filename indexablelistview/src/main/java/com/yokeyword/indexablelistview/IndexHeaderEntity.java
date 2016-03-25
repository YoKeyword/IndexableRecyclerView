package com.yokeyword.indexablelistview;


import java.util.List;

/**
 * Created by YoKeyword on 16/3/21.
 */
public class IndexHeaderEntity<T extends IndexEntity> {
    private String index;
    private String headerTitle;
    private List<T> headerList;

    public IndexHeaderEntity() {
    }

    public IndexHeaderEntity(String index, String headerTitle, List<T> headerList) {
        this.index = index;
        this.headerTitle = headerTitle;
        this.headerList = headerList;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public List<T> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<T> headerList) {
        this.headerList = headerList;
    }
}
