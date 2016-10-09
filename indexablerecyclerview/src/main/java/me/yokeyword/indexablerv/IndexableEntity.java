package me.yokeyword.indexablerv;

/**
 * Created by YoKey on 16/10/9.
 */
public interface IndexableEntity {

    String getIndexByField();

    void setIndexByField(String indexField);

    /**
     * 不是必须实现
     */
    void setIndexByFieldPinyin(String pinyin);
}
