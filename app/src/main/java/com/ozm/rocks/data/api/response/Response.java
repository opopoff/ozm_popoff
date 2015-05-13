package com.ozm.rocks.data.api.response;

import android.support.annotation.Nullable;

public class Response {
    @Nullable
    public final String error;

    public Response(String error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }
}
