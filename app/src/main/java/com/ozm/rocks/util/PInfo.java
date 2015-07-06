package com.ozm.rocks.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class PInfo implements Parcelable {
    private String appname = "";
    private String packagename = "";
    private String versionName = "";
    private int versionCode = 0;
    private Drawable icon;

    public PInfo() {
    }

    public PInfo(String appname, Drawable icon) {
        this.appname = appname;
        this.icon = icon;
    }

    public void setApplicationName(String appname) {
        this.appname = appname;
    }

    public void setPackageName(String pname) {
        this.packagename = pname;
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

    public String getApplicationName() {
        return appname;
    }

    public String getPackageName() {
        return packagename;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PInfo pInfo = (PInfo) o;

        if (versionCode != pInfo.versionCode) return false;
        if (appname != null ? !appname.equals(pInfo.appname) : pInfo.appname != null) return false;
        if (packagename != null ? !packagename.equals(pInfo.packagename) : pInfo.packagename != null)
            return false;
        return !(versionName != null ? !versionName.equals(pInfo.versionName) : pInfo.versionName != null);

    }

    @Override
    public int hashCode() {
        int result = appname != null ? appname.hashCode() : 0;
        result = 31 * result + (packagename != null ? packagename.hashCode() : 0);
        result = 31 * result + (versionName != null ? versionName.hashCode() : 0);
        result = 31 * result + versionCode;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appname);
        dest.writeString(this.packagename);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
//        Bitmap bitmap = ((BitmapDrawable) this.icon).getBitmap();
//        dest.writeParcelable(bitmap, flags);
    }

    protected PInfo(Parcel in) {
        this.appname = in.readString();
        this.packagename = in.readString();
        this.versionName = in.readString();
        this.versionCode = in.readInt();
        Bitmap bitmap = in.readParcelable(getClass().getClassLoader());
//        this.icon = new BitmapDrawable(bitmap);
    }

    public static final Parcelable.Creator<PInfo> CREATOR = new Parcelable.Creator<PInfo>() {
        public PInfo createFromParcel(Parcel source) {
            return new PInfo(source);
        }

        public PInfo[] newArray(int size) {
            return new PInfo[size];
        }
    };
}
