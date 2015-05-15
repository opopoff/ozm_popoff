package com.ozm.rocks.data.api.response;

/**
 * Created by Danil on 14.05.2015.
 */
public class ConfigResponse extends Response {
    public final RestConfig restConfig;

    public ConfigResponse(String error, RestConfig restConfig) {
        super(error);
        this.restConfig = restConfig;
    }
}
