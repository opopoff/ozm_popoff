package com.ozm.rocks.base.mvp;

import com.ozm.rocks.data.api.response.ImageResponse;

public interface BaseView {
    void showLoading();

    void showContent();

    void showError(Throwable throwable);


}
