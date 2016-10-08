package me.yokeyword.sample.contact;

/**
 * Created by YoKey on 16/10/8.
 */
public class UserEntity {
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
}
