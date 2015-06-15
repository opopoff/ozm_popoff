package com.ozm.rocks.data.api;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;

import com.ozm.rocks.ui.ApplicationScope;

@Module(includes = ApiModule.class)
public final class ReleaseApiModule {

    @Provides
    @ApplicationScope
    Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(ApiModule.PRODUCTION_API_URL);
    }
}
