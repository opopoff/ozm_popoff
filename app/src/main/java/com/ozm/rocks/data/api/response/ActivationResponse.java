package com.ozm.rocks.data.api.response;

public final class ActivationResponse extends Response {
    public static final String ALREADY_ACTIVATED = "Already activated";

    public final Order order;

    public ActivationResponse(String error, Order order) {
        super(error);
        this.order = order;
    }
}
