package com.ozm.rocks.data.api;

import com.ozm.rocks.data.api.request.DislikeRequest;
import com.ozm.rocks.data.api.request.HideRequest;
import com.ozm.rocks.data.api.request.LikeRequest;
import com.ozm.rocks.data.api.response.ActivationResponse;
import com.ozm.rocks.data.api.response.AuthResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.PackageRequest;
import com.ozm.rocks.data.api.response.RestConfig;


import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;
import rx.subjects.ReplaySubject;

public interface OzomeApiService {
    @POST("/sign_in.json")
    Observable<AuthResponse> signIn(@Query("user[email]") String email, @Query("user[password]") String password);

    @POST("/merchants/coupons/activate_by_qr.json")
    Observable<ActivationResponse> activateByQr(@Query("qr") String qr);

    @POST("/merchants/coupons/activate_by_barcode.json")
    Observable<ActivationResponse> activateByBarcode(@Query("barcode") String code);

    @POST("/merchants/coupons/activate_by_coupon.json")
    Observable<ActivationResponse> activateByCoupon(@Query("coupon") String coupon, @Query("security") String security);

    @GET("/merchants/coupons/search.json")
    Observable<ActivationResponse> search(@Query("coupon") String coupon);

    // Ozome requests
    @GET("/api/feed/")
    Observable<List<ImageResponse>> getGeneralFeed(@Query("from") int from, @Query("to") int to);

    Observable<List<ImageResponse>> getGeneralFeed(@Query("from") Integer from, @Query("to") Integer to);

    @GET("/api/config/")
    Observable<RestConfig> getConfig();

    @POST("/api/user/send/data/")
    Observable<retrofit.client.Response> sendPackages(@Body PackageRequest packageRequest);

    @GET("/api/feed/update/")
    Observable<String> generalFeedUpdate();

    @GET("/api/feed/{idCategory}/")
    Observable<List<ImageResponse>> getCategoryFeed(@Path("idCategory") int categoryId);

    @GET("/api/feed/personal/")
    Observable<Response> getMyCollection();

    @POST("/api/user/send/actions/")
    Observable<String> postLike(@Body LikeRequest likeRequest);

    @POST("/api/user/send/actions/")
    Observable<String> postDislike(@Body DislikeRequest dislikeRequest);

    @POST("/api/user/send/actions/")
    Observable<String> postHide(@Body HideRequest hideRequest);
}
