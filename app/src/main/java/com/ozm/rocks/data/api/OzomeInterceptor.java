package com.ozm.rocks.data.api;

import com.ozm.rocks.ApplicationScope;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import javax.inject.Inject;

@ApplicationScope
public class OzomeInterceptor implements Interceptor{

    @Inject
    public OzomeInterceptor() {
        // nothing;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        return chain.proceed(requestBuilder.build());
    }
}
