package com.ozm.rocks.ui.main;

import android.graphics.drawable.Drawable;

public class MyCollectionModel {
    private Drawable image;
    private int height;
    private int width;

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
