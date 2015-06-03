package com.ozm.rocks.data;

import android.support.annotation.Nullable;

public interface TokenStorage {
    long apiId();

    @Nullable
    String apiToken();

    void putApiId(long apiId);

    void putApiToken(String apiToken);

    boolean isAuthorized();

    void showWidget(boolean show);

    boolean isShowWidget();

    void updateFirstStart();

    boolean isFirstStart();

    void clear();
}
