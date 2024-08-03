package com.example.coffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.BillItemAdapter;
import com.example.api.ApiService;
import com.example.api.RetrofitClient;
import com.example.model.BillItem;
import com.example.model.OrderDetailDto;
import com.example.model.OrderResponseDto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayScreen extends AppCompatActivity {
    private RecyclerView recyclerViewBillItems;
    private BillItemAdapter billItemAdapter;
    private List<BillItem> billItemList;
    private TextView txtUserInandTimeIn, txtUserOutandTimeOut, tvTableBillId, tvTotalAmount;
    private ApiService apiService;
    private Button btn,btnBack;

    private int orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_screen);

        recyclerViewBillItems = findViewById(R.id.recyclerViewBillItems);
        txtUserInandTimeIn = findViewById(R.id.txtUserInandTimeIn);
        txtUserOutandTimeOut = findViewById(R.id.txtUserOutandTimeOut);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        int areaId = getIntent().getIntExtra("area_id", -1);
        int tableId = getIntent().getIntExtra("table_id", -1);

        // Initialize the list and adapter
        billItemList = new ArrayList<>();
        billItemAdapter = new BillItemAdapter(billItemList);

        // Set up the RecyclerView
        recyclerViewBillItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBillItems.setAdapter(billItemAdapter);

        // Fetch the table ID and use it to call the API
        fetchTableIdAndCallApi(areaId, tableId);

        // Set up button click listener
        btn = findViewById(R.id.btnPay); // Make sure you have a button with this ID in your layout
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment(orderID);
            }
        });
    }

    private void fetchTableIdAndCallApi(int areaId, int tableId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("areas");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot areasSnapshot) {
                boolean areaFound = false;
                for (DataSnapshot areaSnapshot : areasSnapshot.getChildren()) {
                    Integer areaIdFromDb = areaSnapshot.child("id").getValue(Integer.class);

                    if (areaIdFromDb != null && areaIdFromDb.equals(areaId)) {
                        areaFound = true;
                        DatabaseReference tablesRef = areaSnapshot.child("tables").getRef();
                        tablesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot tablesSnapshot) {
                                boolean tableFound = false;
                                for (DataSnapshot tableSnapshot : tablesSnapshot.getChildren()) {
                                    Integer tableIdFromDb = tableSnapshot.child("id").getValue(Integer.class);

                                    if (tableIdFromDb != null && tableIdFromDb.equals(tableId)) {
                                        tableFound = true;
                                        Integer orderId = tableSnapshot.child("orderId").getValue(Integer.class);
                                        orderID = orderId;
                                        if (orderId != null) {
                                            // Call the API with the retrieved orderId
                                            callApiWithOrderId(orderId);
                                        } else {
                                            Toast.makeText(PayScreen.this, "Order ID not found.", Toast.LENGTH_SHORT).show();
                                        }
                                        return;
                                    }
                                }
                                if (!tableFound) {
                                    Toast.makeText(PayScreen.this, "Table not found.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w("Firebase", "loadTables:onCancelled", databaseError.toException());
                            }
                        });
                        break;
                    }
                }
                if (!areaFound) {
                    Toast.makeText(PayScreen.this, "Area not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "loadAreas:onCancelled", databaseError.toException());
            }
        });
    }

    private void callApiWithOrderId(int orderId) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<OrderResponseDto> call = apiService.getOrderDetails(orderId);
        call.enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponseDto orderResponse = response.body();
                    // Handle the response data and update the UI
                    updateUIWithOrderDetails(orderResponse);
                } else {
                    Toast.makeText(PayScreen.this, "Failed to fetch order details. Response is empty.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                Toast.makeText(PayScreen.this, "Network failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithOrderDetails(OrderResponseDto orderResponse) {
        // Define date format patterns
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        // Update the TextViews with the order details
        String userInName = orderResponse.getUserIn().getFirstName();
        String timeIn = formatDate(orderResponse.getTimeIn(), apiDateFormat, displayDateFormat);
        txtUserInandTimeIn.setText(userInName + "\n" + timeIn);

        String userOutName = orderResponse.getUserOut() != null ? orderResponse.getUserOut().getFirstName() : "N/A";
        String timeOut = orderResponse.getTimeOut() != null ? formatDate(orderResponse.getTimeOut(), apiDateFormat, displayDateFormat) : "N/A";
        txtUserOutandTimeOut.setText(userOutName + "\n" + timeOut);

        // Update the bill items list and notify the adapter
        billItemList.clear();
        double totalAmount = 0;

        for (OrderDetailDto orderDetail : orderResponse.getOrderDetails()) {
            // Convert price to a double
            double price = Double.parseDouble(orderDetail.getProductDto().getPrice());

            // Calculate the total price
            double totalPrice = orderDetail.getQuantity() * price;
            totalAmount += totalPrice;

            // Create a new BillItem
            BillItem billItem = new BillItem(
                    orderDetail.getProductDto().getName(),
                    "x" + orderDetail.getQuantity(),
                    String.format("%.2f", price),
                    String.format("%.2f", totalPrice)
            );

            // Add the BillItem to the list
            billItemList.add(billItem);
        }

        // Update total amount
        TextView tvTotalAmount = findViewById(R.id.llTotalAmount).findViewById(R.id.totalAmount);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        currencyFormat.setMaximumFractionDigits(0); // Optional: remove decimal places for VND

// Format the total amount
        String formattedTotalAmount = currencyFormat.format(totalAmount);

// Update the TextView with the formatted total amount
        tvTotalAmount.setText("TỔNG: " + formattedTotalAmount);

        billItemAdapter.notifyDataSetChanged();
    }

    private void makePayment(int orderId) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Call API to fetch order details
        Call<OrderResponseDto> getOrderDetailsCall = apiService.getOrderDetails(orderId);
        getOrderDetailsCall.enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponseDto orderResponse = response.body();
                    String orderStatus = orderResponse.getStatus(); // Assuming you have a getStatus method

                    if ("ORDERING".equals(orderStatus)) {
                        Toast.makeText(PayScreen.this, "Món đang được pha chế", Toast.LENGTH_SHORT).show();
                    } else {
                        // Proceed with payment if status is not ORDERING
                        Call<Void> payCall = apiService.pay(orderId);
                        payCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(PayScreen.this, "Payment successful.", Toast.LENGTH_SHORT).show();
                                    updateTableStatusAfterPayment(orderId);
                                } else {
                                    Toast.makeText(PayScreen.this, "Payment failed.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(PayScreen.this, "Network failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(PayScreen.this, "Failed to fetch order details. Response is empty.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                Toast.makeText(PayScreen.this, "Network failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateTableStatusAfterPayment(int orderId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("areas");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot areasSnapshot) {
                for (DataSnapshot areaSnapshot : areasSnapshot.getChildren()) {
                    DatabaseReference tablesRef = areaSnapshot.child("tables").getRef();
                    tablesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot tablesSnapshot) {
                            for (DataSnapshot tableSnapshot : tablesSnapshot.getChildren()) {
                                Integer tableOrderId = tableSnapshot.child("orderId").getValue(Integer.class);
                                if (tableOrderId != null && tableOrderId == orderId) {
                                    tableSnapshot.getRef().child("orderId").setValue(0);
                                    tableSnapshot.getRef().child("status").setValue("ACTIVE");
                                }
                            }
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                finish();
                            }, 1000);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w("Firebase", "updateTables:onCancelled", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "loadAreas:onCancelled", databaseError.toException());
            }
        });
    }

    private String formatDate(String dateStr, SimpleDateFormat apiDateFormat, SimpleDateFormat displayDateFormat) {
        try {
            Date date = apiDateFormat.parse(dateStr);
            if (date != null) {
                return displayDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}
