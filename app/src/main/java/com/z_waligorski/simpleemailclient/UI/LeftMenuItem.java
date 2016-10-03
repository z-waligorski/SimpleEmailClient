package com.z_waligorski.simpleemailclient.UI;

// Class for single NavigationDrawer item
public class LeftMenuItem {

    private String title;
    private int icon;

    public LeftMenuItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
