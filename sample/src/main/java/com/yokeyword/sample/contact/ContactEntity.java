package com.yokeyword.sample.contact;

import com.yokeyword.indexablelistview.IndexEntity;

/**
 * Created by YoKeyword on 16/3/24.
 */
public class ContactEntity extends IndexEntity {
    private String avatar;
    private String mobile;

    public ContactEntity(String name, String mobile) {
        super(name);
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
}
