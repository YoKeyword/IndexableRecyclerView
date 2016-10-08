package me.yokeyword.sample.contact;

/**
 * Created by YoKey on 16/10/8.
 */

public class MenuEntity {
    private long menuId;
    private String menuTitle;

    public MenuEntity(String title) {
        this.menuTitle = title;
    }

    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }
}
