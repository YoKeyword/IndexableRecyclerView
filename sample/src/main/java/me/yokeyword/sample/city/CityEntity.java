package me.yokeyword.sample.city;

import me.yokeyword.indexablerv.IndexableEntity;

/**
 * Created by YoKey on 16/10/7.
 */
public class CityEntity implements IndexableEntity {
    private long id;
    private String name;

    public CityEntity() {
    }

    public CityEntity(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getIndexField() {
        return name;
    }

    @Override
    public void setIndexField(String indexField) {
        this.name = indexField;
    }

    @Override
    public void setIndexFieldPinyin(String pinyin) {
        // 需要用到拼音时(比如:搜索), 可增添pinyin字段 this.pinyin  = pinyin
        // 见 UserEntity
    }
}
