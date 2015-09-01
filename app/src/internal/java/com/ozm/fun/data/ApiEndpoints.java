package com.ozm.fun.data;


import com.ozm.fun.data.api.ApiEndpoint;

public enum ApiEndpoints {
    INTERNAL("Internal", ApiEndpoint.INTERNAL_API_URL),
    PRODUCTION("Production", ApiEndpoint.PRODUCTION_API_URL),
    CUSTOM("Custom", null);

    public final String name;
    public final String url;

    ApiEndpoints(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public String toString() {
        return name;
    }

    public static ApiEndpoints from(String endpoint) {
        for (ApiEndpoints value : values()) {
            if (value.url != null && value.url.equals(endpoint)) {
                return value;
            }
        }
        return CUSTOM;
    }

    public static boolean isMockMode(String endpoint) {
        return from(endpoint) == INTERNAL;
    }
}
