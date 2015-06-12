package com.ozm.rocks.data.api;

import com.ozm.rocks.data.api.request.RequestDeviceId;
import com.ozm.rocks.data.api.response.RestRegistration;

import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import rx.Observable;

public interface RegistrationApiService {

    public static final String USER_KEY = "mtFxlt3JsW4D5wOl";
    public static final String USER_SECRET = "mu4C3KOi5zhqeMz7xAzkYT0lmrAeXy8JhMtEpd9ln6O7T8dN1aEm4lEY7xLtWqid";

    public static final String URL_REGISTRATION = "/api/register/";

    @POST(URL_REGISTRATION)
    Observable<RestRegistration> register(@Header("Authorization") String header, @Body RequestDeviceId deviceId);

}
