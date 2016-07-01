package com.yokeyword.sample.city;

import me.yokeyword.indexablelistview.IndexEntity;

/**
 * Created by YoKeyword on 16/3/20.
 */
public class CityEntity extends IndexEntity {
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
