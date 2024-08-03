package com.example.coffee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.adapter.OrderAdapter;
import com.example.model.OrderResponseDto;
import com.example.api.ApiService;
import com.example.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderScreen extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderResponseDto> orderList;
    private Button btnBack;
    @Override
    protected void onResume() {
        super.onResume();
        fetchOrders();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_screen);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        recyclerView.setAdapter(orderAdapter);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        fetchOrders();
    }

    private void fetchOrders() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<List<OrderResponseDto>> call = apiService.getOrders();

        call.enqueue(new Callback<List<OrderResponseDto>>() {
            @Override
            public void onResponse(Call<List<OrderResponseDto>> call, Response<List<OrderResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderResponseDto> orders = response.body();
                    orderList.clear();

                    for (OrderResponseDto order : orders) {
                        // Check the status before adding to the list
                        Log.d("Order", "ID: " + order.getId() + ", Status: " + order.getStatus() + ", Other details: " + order.toString());
                        if (!"PAY".equals(order.getStatus()) && !"SERVE".equals(order.getStatus())) {
                            orderList.add(order);
                        }
                    }

                    orderAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(OrderScreen.this, "Failed to fetch orders.", Toast.LENGTH_SHORT).show();
                    Log.d("error", response.message());
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponseDto>> call, Throwable t) {
                Toast.makeText(OrderScreen.this, "Network failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("error", t.getMessage());
            }
        });
    }

}
