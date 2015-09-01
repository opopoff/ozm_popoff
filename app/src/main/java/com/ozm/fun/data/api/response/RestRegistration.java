package com.ozm.fun.data.api.response;

public class RestRegistration {

    public final String key;
    public final String secret;
    public final String detail;

    public RestRegistration(String key, String secret, String detail) {
        this.key = key;
        this.secret = secret;
        this.detail = detail;
    }

}
