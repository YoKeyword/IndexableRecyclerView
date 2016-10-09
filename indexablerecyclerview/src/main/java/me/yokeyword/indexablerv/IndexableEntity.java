package me.yokeyword.indexablerv;

/**
 * Created by YoKey on 16/10/9.
 */
public interface IndexableEntity {

    String getIndexField();

    void setIndexField(String indexField);

    /**
     * 不是必须实现
     */
    void setIndexFieldPinyin(String pinyin);
}
