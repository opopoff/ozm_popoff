package com.ozm.rocks.data;

import android.support.annotation.Nullable;

public interface TokenStorage {
    @Nullable
    String getUserKey();

    @Nullable
    String getUserSecret();

    void putUserKey(String userKey);

    void putUserSecret(String userSecret);

    boolean isAuthorized();

    void showWidget(boolean show);

    boolean isShowWidget();

    void clear();
}
