package me.yokeyword.indexablerv;

/**
 * Created by YoKey on 16/10/6.
 */
class EntityWrapper<T> {
    static final int TYPE_TITLE = Integer.MAX_VALUE - 1;
    static final int TYPE_CONTENT = Integer.MAX_VALUE;

    private String index;
    private String indexTitle;
    private String pinyin;
    private String indexByField;
    private T data;
    private int originalPosition = -1;
    private int itemType = TYPE_CONTENT;

    EntityWrapper() {
    }

    EntityWrapper(String index, int itemType) {
        this.index = index;
        this.indexTitle = index;
        this.pinyin = index;
        this.itemType = itemType;
    }

    String getIndex() {
        return index;
    }

    void setIndex(String index) {
        this.index = index;
    }

    public String getIndexTitle() {
        return indexTitle;
    }

    public void setIndexTitle(String indexTitle) {
        this.indexTitle = indexTitle;
    }

    public String getPinyin() {
        return pinyin;
    }

    void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getIndexByField() {
        return indexByField;
    }

    void setIndexByField(String indexByField) {
        this.indexByField = indexByField;
    }

    T getData() {
        return data;
    }

    void setData(T data) {
        this.data = data;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    void setOriginalPosition(int originalPosition) {
        this.originalPosition = originalPosition;
    }

    int getItemType() {
        return itemType;
    }

    void setItemType(int itemType) {
        this.itemType = itemType;
    }
}