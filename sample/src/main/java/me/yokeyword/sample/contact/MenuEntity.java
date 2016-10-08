package me.yokeyword.sample.contact;

/**
 * Created by YoKey on 16/10/8.
 */

public class MenuEntity {
    private long menuId;
    private String menuTitle;
    private int menuIconRes;

    public MenuEntity(String title, int iconRes) {
        this.menuTitle = title;
        this.menuIconRes = iconRes;
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

    public int getMenuIconRes() {
        return menuIconRes;
    }

    public void setMenuIconRes(int menuIconRes) {
        this.menuIconRes = menuIconRes;
    }
}
