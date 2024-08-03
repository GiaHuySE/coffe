package com.example.api;



import com.example.model.AreaResponse;
import com.example.model.LoginRequest;
import com.example.model.OrderRequest;
import com.example.model.OrderResponse;
import com.example.model.OrderResponseDto;
import com.example.model.ProductResponse;
import com.example.model.RefreshTokenRequest;
import com.example.model.TokenResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/login")
    Call<TokenResponse> login(@Body LoginRequest loginRequest);

    @GET("api/area") // Adjust endpoint as needed
    Call<List<AreaResponse>> getAreas();

    @GET("api/product")
    Call<List<ProductResponse>> getProducts();

    @POST("/api/order")
    Call<OrderResponse> postOrder(@Body OrderRequest orderRequest);
    @GET("api/order/{id}")
    Call<OrderResponseDto> getOrderDetails(@Path("id") int orderId);

    @GET("/api/order")
    Call<List<OrderResponseDto>> getOrders();

    @POST("/api/order/pay/{id}")
    Call<Void> pay(@Path("id") int ordetId);

    @POST("/api/order/update/{id}")
    Call<Void> updateOrderStatus(@Path("id") int orderId, @Body OrderRequest orderRequest);
}
