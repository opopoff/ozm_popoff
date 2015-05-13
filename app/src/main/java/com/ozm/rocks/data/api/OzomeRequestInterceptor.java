package com.ozm.rocks.data.api;

import android.annotation.SuppressLint;

import com.ozm.rocks.data.Clock;
import com.ozm.rocks.data.TokenStorage;
import com.ozm.rocks.ui.ApplicationScope;
import com.ozm.rocks.util.Strings;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;


@ApplicationScope
public class OzomeRequestInterceptor implements Interceptor {
    private static final String SIGNATURE_FORMAT = "%d_%s_%d";
    private static final String API_ID_KEY = "api_id";
    private static final String SIGNATURE_KEY = "signature";
    private static final String TIMESTAMP_KEY = "timestamp";

    private final TokenStorage tokenStorage;
    private final Clock clock;

    @Inject
    public OzomeRequestInterceptor(TokenStorage tokenStorage, Clock clock) {
        this.tokenStorage = tokenStorage;
        this.clock = clock;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        if (!tokenStorage.isAuthorized())
            return chain.proceed(request);
        final Request newRequest = trasformRequest(request);
        return chain.proceed(newRequest);
    }

    @SuppressLint("DefaultLocale")
    Request trasformRequest(Request request) {
        final Request.Builder requestBuilder = request.newBuilder();

        final long apiId = tokenStorage.apiId();
        final String apiToken = tokenStorage.apiToken();
        final long unixTime = clock.unixTime();
        final String signature = String.format(SIGNATURE_FORMAT, apiId, apiToken, unixTime);
        final String md5Signature = Strings.md5(signature);
        final URI uri = URI.create(request.urlString());
        StringBuilder uriBuilder = new StringBuilder(uri.toString());
        if (uri.getQuery() == null) {
            uriBuilder.append('?');
        } else {
            uriBuilder.append('&');
        }
        appendQueryParameter(uriBuilder, API_ID_KEY, String.valueOf(apiId));
        uriBuilder.append('&');
        appendQueryParameter(uriBuilder, SIGNATURE_KEY, md5Signature);
        uriBuilder.append('&');
        appendQueryParameter(uriBuilder, TIMESTAMP_KEY, String.valueOf(unixTime));

        requestBuilder.url(uriBuilder.toString());
        return requestBuilder.build();
    }

    private void appendQueryParameter(StringBuilder uriBuilder, String key, String value) {
        uriBuilder.append(key);
        uriBuilder.append('=');
        uriBuilder.append(value);
    }
}
