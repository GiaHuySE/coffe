package com.example.interceptor;

import android.content.Context;
import android.content.Intent;

import com.example.coffee.MainActivity;
import com.example.untils.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    private final Context context;

    public AuthenticationInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Add the access token to the request
        String accessToken = TokenManager.getAccessToken(context);
        if (accessToken != null) {
            // Add authorization header with Bearer token
            Request modifiedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            // Proceed with the modified request
            Response response = chain.proceed(modifiedRequest);

            if (response.code() == 401) {
                // Token is expired or invalid
                // Clear tokens
                TokenManager.clearTokens(context);

                // Redirect to login screen
                Intent loginIntent = new Intent(context, MainActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(loginIntent);
            }

            return response;
        } else {
            // No access token available, just proceed with the request
            return chain.proceed(originalRequest);
        }
    }
}
