package me.yokeyword.sample.contact;

import me.yokeyword.indexablerv.IndexableEntity;

/**
 * Created by YoKey on 16/10/8.
 */
public class UserEntity implements IndexableEntity {
    private String nick;
    private String avatar;
    private String mobile;

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

    @Override
    public String getFieldIndexBy() {
        return nick;
    }

    @Override
    public void setFieldIndexBy(String indexField) {
        this.nick = indexField;
    }

    @Override
    public void setFieldPinyinIndexBy(String pinyin) {
        // 需要用到拼音时(比如:搜索), 可增添pinyin字段 this.pinyin  = pinyin
        // 见 CityEntity
    }
}
