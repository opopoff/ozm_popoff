package com.ozm.rocks.data.api.response;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public final class AuthResponse extends Response {
    @Nullable
    public final User user;

    public AuthResponse(String error, @Nullable User user) {
        super(error);
        this.user = user;
    }

    public static final class User {
        public final long id;
        @SerializedName("api_id")
        public final long apiId;
        @SerializedName("api_token")
        public final String apiToken;

        public User(long id, long apiId, String apiToken) {
            this.id = id;
            this.apiId = apiId;
            this.apiToken = apiToken;
        }
    }
}
