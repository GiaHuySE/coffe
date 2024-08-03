package com.example.coffee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.api.ApiService;
import com.example.api.RetrofitClient;
import com.example.model.OrderDetailDto;
import com.example.model.OrderResponseDto;
import com.example.model.Recipe;
import com.example.model.OrderRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeScreen extends AppCompatActivity {

    private LinearLayout productLayout;
    private int orderId;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_screen);

        productLayout = findViewById(R.id.productLayout);
        Button doneButton = findViewById(R.id.doneButton);
        btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus(orderId);
            }
        });

        orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId != -1) {
            fetchOrderDetails(orderId);
        } else {
            Log.e("RecipeScreen", "Invalid order ID.");
        }
    }

    private void fetchOrderDetails(int orderId) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<OrderResponseDto> call = apiService.getOrderDetails(orderId);

        call.enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponseDto order = response.body();
                    displayOrderDetails(order);
                } else {
                    Log.e("RecipeScreen", "Failed to fetch order details.");
                }
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                Log.e("RecipeScreen", "Network failure: " + t.getMessage());
            }
        });
    }

    private void displayOrderDetails(OrderResponseDto order) {
        for (OrderDetailDto orderDetail : order.getOrderDetails()) {
            if (orderDetail.getProductDto() != null) {
                LinearLayout productItemLayout = new LinearLayout(this);
                productItemLayout.setOrientation(LinearLayout.VERTICAL);
                productItemLayout.setPadding(8, 8, 8, 8);
                productItemLayout.setBackgroundResource(R.drawable.product_background);
                productItemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView productTextView = new TextView(this);
                productTextView.setText(orderDetail.getProductDto().getName() + " - Số lượng: " + orderDetail.getQuantity());
                productTextView.setTextSize(18);
                productTextView.setTextColor(getResources().getColor(R.color.primaryTextColor));
                productTextView.setPadding(0, 8, 0, 8);

                productItemLayout.addView(productTextView);

                if (orderDetail.getProductDto().getRecipes() != null) {
                    for (Recipe recipe : orderDetail.getProductDto().getRecipes()) {
                        if (recipe.getIngredient() != null) {
                            TextView ingredientTextView = new TextView(this);
                            ingredientTextView.setText("   " + recipe.getIngredient().getName() + " - Số lượng: " + recipe.getQuantity());
                            ingredientTextView.setTextSize(16);
                            ingredientTextView.setTextColor(getResources().getColor(R.color.primaryTextColor));
                            ingredientTextView.setPadding(0, 4, 0, 4);

                            productItemLayout.addView(ingredientTextView);
                        }
                    }
                }

                productLayout.addView(productItemLayout);
            }
        }
    }

    private void updateOrderStatus(int orderId) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Create an OrderRequest object (ensure it's initialized with necessary fields)
        OrderRequest orderRequest = new OrderRequest(null,"SERVE",null);
        // Example field initialization (adjust according to your OrderRequest definition)

        Call<Void> call = apiService.updateOrderStatus(orderId, orderRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        finish();
                    }, 1000);
                } else {
                    Log.e("RecipeScreen", "Failed to update order status.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("RecipeScreen", "Network failure: " + t.getMessage());
            }
        });
    }
}
