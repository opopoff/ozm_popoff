package com.umad.wat.base.mvp;

public interface BaseView {
    void showLoading();

    void showContent();

    void showError(Throwable throwable);


}
