package com.ozm.rocks.data.api;

import android.content.SharedPreferences;

import com.ozm.rocks.data.ApiEndpoint;
import com.ozm.rocks.data.prefs.StringPreference;
import com.ozm.rocks.ui.ApplicationScope;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.MockRestAdapter;
import retrofit.RestAdapter;
import retrofit.android.AndroidMockValuePersistence;

@Module(includes = ApiModule.class)
public final class DebugApiModule {

    @Provides
    @ApplicationScope
    Endpoint provideEndpoint(@ApiEndpoint StringPreference apiEndpoint) {
        return Endpoints.newFixedEndpoint(apiEndpoint.get());
    }

    @Provides
    @OzomeApiQualifier
    @ApplicationScope
    MockRestAdapter provideMockRestAdapter(@OzomeApiQualifier RestAdapter restAdapter, SharedPreferences preferences) {
        MockRestAdapter mockRestAdapter = MockRestAdapter.from(restAdapter);
        AndroidMockValuePersistence.install(mockRestAdapter, preferences);
        return mockRestAdapter;
    }
}
