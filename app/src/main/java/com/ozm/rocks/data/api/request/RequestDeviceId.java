package com.ozm.rocks.data.api.request;

public class RequestDeviceId {

    private String deviceId;

    public RequestDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
