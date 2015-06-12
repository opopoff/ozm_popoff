package com.ozm.rocks.data.api.response;

import android.support.annotation.Nullable;

public class Response {
    @Nullable
    public final String detail;

    public Response(String detail) {
        this.detail = detail;
    }

    public boolean hasError() {
        return detail != null;
    }
}
