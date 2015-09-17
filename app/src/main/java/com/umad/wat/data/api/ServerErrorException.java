package com.umad.wat.data.api;

import java.util.Locale;

import retrofit.RetrofitError;

public class ServerErrorException extends Exception {

    public static final int ERROR_TOKEN_INVALID = 401;
    public static final int ERROR_TOKEN_EXPIRED = 403;

    public static final String MESSAGE_FORMAT = "ERROR %d - %s (code %d): %s";

    private final RetrofitError cause;
    private final int errorCode;
    private final String detailMessage;

    public ServerErrorException(RetrofitError cause, int errorCode, String detailMessage) {
        super(detailMessage, cause);
        this.cause = cause;
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    @Override
    public String getMessage() {
        int status = cause.getResponse().getStatus();
        String reason = cause.getResponse().getReason();
        return String.format(Locale.getDefault(), MESSAGE_FORMAT, status, reason, errorCode,
                super.getMessage());
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getSimpleMessage() {
        return detailMessage;
    }
}
