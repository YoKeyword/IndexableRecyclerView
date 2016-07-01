package com.yokeyword.sample.contact;

import me.yokeyword.indexablelistview.IndexEntity;

/**
 * Created by YoKeyword on 16/3/24.
 */
public class ContactEntity extends IndexEntity {
    private String name;
    private String avatar;
    private String mobile;

    public ContactEntity(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
