package com.umad.wat.data.api;

import android.content.SharedPreferences;

import com.umad.wat.data.ApiEndpoint;
import com.umad.wat.data.prefs.StringPreference;
import com.umad.wat.ApplicationScope;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.MockRestAdapter;
import retrofit.RestAdapter;
import retrofit.android.AndroidMockValuePersistence;

@Module(includes = ApiModule.class)
public final class DebugApiModule {

//    @Provides
//    @ApplicationScope
//    @EndPointQualifier
//    String provideEndPoint() {
//        return "http://ozm.rocks:49124";
//    }

    @Provides
    @ApplicationScope
    Endpoint provideEndpoint(@ApiEndpoint StringPreference apiEndpoint) {
        return Endpoints.newFixedEndpoint(apiEndpoint.get());
    }

    @Provides
    @ApplicationScope
    MockRestAdapter provideMockRestAdapter(RestAdapter restAdapter, SharedPreferences preferences) {
        MockRestAdapter mockRestAdapter = MockRestAdapter.from(restAdapter);
        AndroidMockValuePersistence.install(mockRestAdapter, preferences);
        return mockRestAdapter;
    }
}
