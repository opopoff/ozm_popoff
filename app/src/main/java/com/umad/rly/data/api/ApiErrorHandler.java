package com.umad.rly.data.api;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.ozm.BuildConfig;
import com.umad.rly.data.api.response.Response;
import com.umad.rly.ApplicationScope;

import javax.inject.Inject;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

@ApplicationScope
public class ApiErrorHandler implements ErrorHandler {

    private final Application application;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Inject
    public ApiErrorHandler(Application application) {
        this.application = application;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        Response response = null;

        ServerErrorException exception = null;
        int errorCode = cause.getResponse().getStatus();
        try {
            response = (Response) cause.getBodyAs(Response.class);
        } catch (Exception e) {
            exception = new ServerErrorException(cause, errorCode, "Unknown error");
        }
        if (exception == null) {
            exception = new ServerErrorException(cause, errorCode, response.detail);
        }
        if (BuildConfig.DEBUG) {
            final String message = exception.getMessage();
            final String url = cause.getUrl();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(application, message + "url: " + url, Toast.LENGTH_LONG).show();
                }
            });
        }
        return exception;
    }
}
