package com.umad.rly.base.mvp;

public interface BaseView {
    void showLoading();

    void showContent();

    void showError(Throwable throwable);


}
