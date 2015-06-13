package com.ozm.rocks.data.api;

import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.request.ShareRequest;
import com.ozm.rocks.data.api.response.CategoryResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.PackageRequest;
import com.ozm.rocks.data.api.response.RestConfig;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface OzomeApiService {

    public static final String URL_FEED = "/api/feed/";
    public static final String URL_CONFIG = "/api/config/";
    public static final String URL_SEND_DATA = "/api/user/send/data/";
    public static final String URL_FEED_UPDATE = "/api/feed/update/";
    public static final String URL_CATEGORY_FEED_UPDATE = "/api/feed/update/{idCategory}/";
    public static final String URL_CATEGORY_FEED = "/api/feed/{idCategory}/";
    public static final String URL_PERSONAL = "/api/feed/personal/";
    public static final String URL_SEND_ACTIONS = "/api/user/send/actions/";
    public static final String URL_CATEGORIES = "/api/categories/";
    public static final String URL_GOLDEN = "/api/feed/personal/golden/{categoryId}/";

    @GET(URL_FEED)
    Observable<List<ImageResponse>> getGeneralFeed(@Query("from") int from, @Query("to") int to);

    @GET(URL_CONFIG)
    Observable<RestConfig> getConfig();

    @POST(URL_SEND_DATA)
    Observable<retrofit.client.Response> sendPackages(@Body PackageRequest packageRequest);

    @GET(URL_FEED_UPDATE)
    Observable<String> generalFeedUpdate();

    @GET(URL_CATEGORY_FEED_UPDATE)
    Observable<String> categoryFeedUpdate(@Path("idCategory") long categoryId);

    @GET(URL_CATEGORY_FEED)
    Observable<List<ImageResponse>> getCategoryFeed(
            @Path("idCategory") long categoryId, @Query("from") int from, @Query("to") int to);

    @GET(URL_PERSONAL)
    Observable<List<ImageResponse>> getMyCollection();

    @POST(URL_SEND_ACTIONS)
    Observable<String> postLike(@Body LikeRequest likeRequest);

    @POST(URL_SEND_ACTIONS)
    Observable<String> postDislike(@Body DislikeRequest dislikeRequest);

    @POST(URL_SEND_ACTIONS)
    Observable<String> postHide(@Body HideRequest hideRequest);

    @POST(URL_SEND_ACTIONS)
    Observable<String> postShare(@Body ShareRequest shareRequest);

    @GET(URL_CATEGORIES)
    Observable<CategoryResponse> getCategories();

    @GET(URL_GOLDEN)
    Observable<List<ImageResponse>> getGoldFeed(
            @Path("categoryId") long categoryId, @Query("from") int from, @Query("to") int to);

}
