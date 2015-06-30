package com.ozm.rocks.data.api.request;

public class SettingRequest {

    boolean obsceneDisabled;

    public SettingRequest(boolean obsceneDisabled) {
        this.obsceneDisabled = obsceneDisabled;
    }

    public boolean isObsceneDisabled() {
        return obsceneDisabled;
    }
}
