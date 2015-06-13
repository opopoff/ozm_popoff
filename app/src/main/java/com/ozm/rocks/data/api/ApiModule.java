package com.ozm.rocks.data.api;

import com.ozm.BuildConfig;
import com.ozm.rocks.ui.ApplicationScope;
import com.squareup.okhttp.OkHttpClient;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;

@Module
public final class ApiModule {
    public static final String PRODUCTION_API_URL = "http://52.28.1.212:49124";

    @Provides
    @ApplicationScope
    Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    @Provides
    @ApplicationScope
    RestAdapter provideRestAdapterRegistrationApi(Endpoint endpoint, Client client, ApiErrorHandler apiErrorHandler,
                                   RequestInterceptor requestInterceptor) {
        return new RestAdapter.Builder().
                //TODO delete log level param
                setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL
                        : RestAdapter.LogLevel.BASIC).
                setRequestInterceptor(requestInterceptor).
                setClient(client).
                setEndpoint(endpoint).
                setErrorHandler(apiErrorHandler).
                build();
    }

    @Provides
    @ApplicationScope
    RequestInterceptor provideRequestInterceptorRegistrationApi() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                //TODO change authorization header
                request.addHeader("Content-Type", "application/json");
            }
        };
    }

    @Provides
    @ApplicationScope
    OzomeApiService provideRegistrationApiService(RestAdapter adapter) {
        return adapter.create(OzomeApiService.class);
    }

}
