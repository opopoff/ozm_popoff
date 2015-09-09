package com.umad.rly.data.api;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozm.BuildConfig;
import com.umad.rly.ApplicationScope;
import com.squareup.okhttp.OkHttpClient;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module
public final class ApiModule {

    @Provides
    @ApplicationScope
    Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    @Provides
    @ApplicationScope
    Gson provideGson() {
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
//                        return f.getDeclaringClass().equals(RealmObject.class);
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    @Provides
    @ApplicationScope
    GsonConverter provideGsonConverter(Gson gson) {
        return new GsonConverter(gson);
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
