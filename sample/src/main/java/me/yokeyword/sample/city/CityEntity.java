package me.yokeyword.sample.city;

import me.yokeyword.indexablerv.IndexableEntity;

/**
 * Created by YoKey on 16/10/7.
 */
public class CityEntity implements IndexableEntity {
    private long id;
    private String name;
    private String pinyin;

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

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public String getIndexByField() {
        return name;
    }

    @Override
    public void setIndexByField(String indexByField) {
        this.name = indexByField;
    }

    @Override
    public void setIndexByFieldPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
