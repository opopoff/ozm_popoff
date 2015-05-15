package com.ozm.rocks.data.api;

import com.ozm.rocks.data.api.response.ActivationResponse;
import com.ozm.rocks.data.api.response.AuthResponse;
import com.ozm.rocks.data.api.response.ImageResponse;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
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
    Observable<List<ImageResponse>> getGeneralFeed(@Query("from") int from, @Query("to") int to);

    @GET("/api/feed/update/")
    Observable<String> generalFeedUpdate();

    @GET("/api/feed/{idCategory}/")
    Observable<List<ImageResponse>> getCategoryFeed(@Path("idCategory") int categoryId);

    @GET("/api/feed/personal/")
    Observable<Response> getMyCollection();
}
