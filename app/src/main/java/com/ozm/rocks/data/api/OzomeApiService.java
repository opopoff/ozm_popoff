package com.ozm.rocks.data.api;

import com.ozm.rocks.data.api.response.ActivationResponse;
import com.ozm.rocks.data.api.response.AuthResponse;
import com.ozm.rocks.data.api.response.ImageResponse;
import com.ozm.rocks.data.api.response.PackageRequest;
import com.ozm.rocks.data.api.response.RestConfig;


import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

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
    Observable<List<ImageResponse>> getGeneralFeed(@Query("from") Integer from, @Query("to") Integer to);

    @GET("/api/config/")
    Observable<RestConfig> getConfig();

    @POST("/api/user/send/data/")
    Observable<retrofit.client.Response> sendPackages(@Body PackageRequest packageRequest);
}
