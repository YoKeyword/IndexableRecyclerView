package me.yokeyword.sample.contact;

import me.yokeyword.indexablerv.IndexableEntity;

/**
 * Created by YoKey on 16/10/8.
 */
public class UserEntity implements IndexableEntity {
    private String nick;
    private String avatar;
    private String mobile;
    private String pinyin;

    public UserEntity(String nick, String mobile) {
        this.nick = nick;
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

    public String getNick() {
        return nick;
    }

    public void setNick(String name) {
        this.nick = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public String getIndexField() {
        return nick;
    }

    @Override
    public void setIndexField(String indexField) {
        this.nick = indexField;
    }

    @Override
    public void setIndexFieldPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
