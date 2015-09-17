package com.umad.wat.data.api;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;

import com.umad.wat.ApplicationScope;

@Module(includes = ApiModule.class)
public final class ReleaseApiModule {

    @Provides
    @ApplicationScope
    Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(ApiEndpoint.PRODUCTION_API_URL);
    }
}
