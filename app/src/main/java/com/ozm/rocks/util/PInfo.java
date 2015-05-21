package com.ozm.rocks.util;

import android.graphics.drawable.Drawable;

public class PInfo {
    private String appname = "";
    private String pname = "";
    private String versionName = "";
    private int versionCode = 0;
    private Drawable icon;

    public PInfo() {
    }

    public PInfo(String appname, Drawable icon) {
        this.appname = appname;
        this.icon = icon;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppname() {
        return appname;
    }

    public String getPname() {
        return pname;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public Drawable getIcon() {
        return icon;
    }
}
