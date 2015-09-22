package com.umad.wat.data.api;

import com.umad.wat.data.api.request.CategoryPinRequest;
import com.umad.wat.data.api.request.DislikeRequest;
import com.umad.wat.data.api.request.HideRequest;
import com.umad.wat.data.api.request.LikeRequest;
import com.umad.wat.data.api.request.RequestDeviceId;
import com.umad.wat.data.api.request.SettingRequest;
import com.umad.wat.data.api.request.ShareRequest;
import com.umad.wat.data.api.response.CategoryResponse;
import com.umad.wat.data.api.response.ImageResponse;
import com.umad.wat.data.api.response.PackageRequest;
import com.umad.wat.data.api.response.RestConfig;
import com.umad.wat.data.api.response.RestRegistration;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface OzomeApiService {

    public static final String REGISTRY_USER_KEY = "mtFxlt3JsW4D5wOl";
    public static final String REGISTRY_USER_SECRET =
            "mu4C3KOi5zhqeMz7xAzkYT0lmrAeXy8JhMtEpd9ln6O7T8dN1aEm4lEY7xLtWqid";

    public static final String URL_REGISTRATION         = "/api/register/";
    public static final String URL_CONFIG               = "/api/config/";
    public static final String URL_SEND_DATA            = "/api/user/send/data/";
    public static final String URL_PERSONAL             = "/api/feed/personal/";
    public static final String URL_CATEGORIES           = "/api/categories/";
    public static final String URL_SEND_ACTIONS         = "/api/user/send/actions/";
    public static final String URL_GOLDEN               = "/api/feed/personal/golden/{idCategory}/";
    public static final String URL_CATEGORY_FEED        = "/api/feed/{idCategory}/";
    public static final String URL_SEND_SETTINGS        = "/api/user/send/settings/";
    public static final String URL_FEED                 = "/api/feed/";
    public static final String URL_FEED_UPDATE          = "/api/feed/update/";

    public static final String HEADER_AUTH = "Authorization";

    public static final String PARAM_CATEGORY = "idCategory";
    public static final String PARAM_FROM = "from";
    public static final String PARAM_TO = "to";

    @POST(URL_REGISTRATION)
    Observable<RestRegistration> register(
            @Header(HEADER_AUTH) String header,
            @Body RequestDeviceId deviceId
    );

    @GET(URL_CONFIG)
    Observable<RestConfig> getConfig(
            @Header(HEADER_AUTH) String header
    );

    @POST(URL_SEND_DATA)
    Observable<Response> sendPackages(
            @Header(HEADER_AUTH) String header,
            @Body PackageRequest packageRequest
    );

    @GET(URL_PERSONAL)
    Observable<List<ImageResponse>> getPersonalFeed(
            @Header(HEADER_AUTH) String header
    );

    @GET(URL_CATEGORIES)
    Observable<CategoryResponse> getCategories(
            @Header(HEADER_AUTH) String header
    );

    @POST(URL_SEND_ACTIONS)
    Observable<String> postLike(
            @Header(HEADER_AUTH) String header,
            @Body LikeRequest likeRequest
    );

    @POST(URL_SEND_ACTIONS)
    Observable<String> postDislike(
            @Header(HEADER_AUTH) String header,
            @Body DislikeRequest dislikeRequest
    );

    @POST(URL_SEND_ACTIONS)
    Observable<String> postHide(
            @Header(HEADER_AUTH) String header,
            @Body HideRequest hideRequest
    );

    @POST(URL_SEND_ACTIONS)
    Observable<String> postShare(
            @Header(HEADER_AUTH) String header,
            @Body ShareRequest shareRequest
    );

    @POST(URL_SEND_ACTIONS)
    Observable<String> pin(
            @Header(HEADER_AUTH) String header,
            @Body CategoryPinRequest categoryPinRequest
    );

    @GET(URL_GOLDEN)
    Observable<List<ImageResponse>> getGoldFeed(
            @Header(HEADER_AUTH) String header,
            @Path(PARAM_CATEGORY) long categoryId,
            @Query(PARAM_FROM) int from,
            @Query(PARAM_TO) int to
    );

    @GET(URL_CATEGORY_FEED)
    Observable<List<ImageResponse>> getCategoryFeed(
            @Header(HEADER_AUTH) String header,
            @Path(PARAM_CATEGORY) long categoryId,
            @Query(PARAM_FROM) int from,
            @Query(PARAM_TO) int to
    );

    @POST(URL_SEND_SETTINGS)
    Observable<String> sendCensorshipSetting(
            @Header(HEADER_AUTH) String header,
            @Body SettingRequest settingRequest
    );

    @GET(URL_FEED)
    Observable<List<ImageResponse>> getGeneralFeed(
            @Header(HEADER_AUTH) String header,
            @Query(PARAM_FROM) int from,
            @Query(PARAM_TO) int to
    );

    @GET(URL_FEED_UPDATE)
    Observable<String> generalFeedUpdate(
            @Header(HEADER_AUTH) String header
    );
}
