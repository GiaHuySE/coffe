package com.example.coffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.adapter.AreaAdapter;
import com.example.adapter.ProductAdapter;
import com.example.adapter.TableAdapter;
import com.example.api.ApiService;
import com.example.api.RetrofitClient;
import com.example.model.AreaResponse;
import com.example.model.OrderDetail;
import com.example.model.OrderRequest;
import com.example.model.OrderResponse;
import com.example.model.ProductResponse;
import com.example.model.Table;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectProductScreen extends AppCompatActivity implements ProductAdapter.CartListener {

    private static final String TAG = "SelectProductScreen";
    private static final String AREA_REF = "areas";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_SERVING = "SERVING";

    private RecyclerView recyclerViewProducts;
    private ProductAdapter adapter;
    private Button btnCheckout, btnOrder ,btnChuyenBan,btnBack;
    private List<ProductResponse> productList = new ArrayList<>();
    private Map<ProductResponse, Integer> cart = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product_screen);

        initViews();
        setupRecyclerView();
        fetchProducts();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int areaId = getIntent().getIntExtra("area_id", -1);
                int tableId = getIntent().getIntExtra("table_id", -1);
                checkTableStatusBeforeCheckout(areaId, tableId);
            }
        });btnChuyenBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectAreaDialog();
            }
        });
    }

    private void initViews() {
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnOrder = findViewById(R.id.btnOrder);
        btnChuyenBan = findViewById(R.id.btnSelectAreaTable);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(productList, this);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(adapter);
    }

    @Override
    public void onAddToCart(ProductResponse product, int quantity) {
        if (quantity > 0) {
            cart.put(product, quantity);
        } else if (quantity == 0) {
            cart.remove(product);
        }
        btnOrder.setText(String.format("Order (%d)", cart.size()));
    }
    private void fetchProducts() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getProducts().enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SelectProductScreen.this, "Không có sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                Toast.makeText(SelectProductScreen.this, "Không có sản phẩm: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("product",t.getMessage());
            }
        });
    }

    private void checkTableStatusBeforeCheckout(int areaId, int tableId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AREA_REF);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot areasSnapshot) {
                for (DataSnapshot areaSnapshot : areasSnapshot.getChildren()) {
                    Integer areaIdFromDb = areaSnapshot.child("id").getValue(Integer.class);
                    if (areaIdFromDb != null && areaIdFromDb.equals(areaId)) {
                        checkTableStatus(areaSnapshot, tableId);
                        return;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadAreas:onCancelled", databaseError.toException());
            }
        });
    }

    private void checkTableStatus(DataSnapshot areaSnapshot, int tableId) {
        DatabaseReference tablesRef = areaSnapshot.child("tables").getRef();
        tablesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot tablesSnapshot) {
                for (DataSnapshot tableSnapshot : tablesSnapshot.getChildren()) {
                    Integer tableIdFromDb = tableSnapshot.child("id").getValue(Integer.class);
                    if (tableIdFromDb != null && tableIdFromDb.equals(tableId)) {
                        String status = tableSnapshot.child("status").getValue(String.class);
                        if (STATUS_ACTIVE.equals(status)) {
                            Toast.makeText(SelectProductScreen.this, "Bàn này chưa có order", Toast.LENGTH_SHORT).show();
                        } else {
                            proceedToPayScreen();
                        }
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadTables:onCancelled", databaseError.toException());
            }
        });
    }

    public interface OnOrderIdReceivedListener {
        void onOrderIdReceived(int orderId);
    }


    private void getOrderId(int areaId, int tableId, final OnOrderIdReceivedListener listener) {
        DatabaseReference areaRef = FirebaseDatabase.getInstance().getReference(AREA_REF);
        areaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot areasSnapshot) {
                for (DataSnapshot areaSnapshot : areasSnapshot.getChildren()) {
                    Integer areaIdFromDb = areaSnapshot.child("id").getValue(Integer.class);
                    if (areaIdFromDb != null && areaIdFromDb.equals(areaId)) {
                        for (DataSnapshot tableSnapshot : areaSnapshot.child("tables").getChildren()) {
                            Integer tableIdFromDb = tableSnapshot.child("id").getValue(Integer.class);
                            if (tableIdFromDb != null && tableIdFromDb.equals(tableId)) {
                                Integer orderId = tableSnapshot.child("orderId").getValue(Integer.class);
                                Log.d("id", String.valueOf(orderId));
                                listener.onOrderIdReceived(orderId != null ? orderId : -1);
                                return;
                            }
                        }
                        listener.onOrderIdReceived(-1);
                        return;
                    }
                }
                listener.onOrderIdReceived(-1); // Area not found
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to get order ID", databaseError.toException());
                listener.onOrderIdReceived(-1);
            }
        });
    }


    private void proceedToPayScreen() {
        int areaId = getIntent().getIntExtra("area_id", -1);
        int tableId = getIntent().getIntExtra("table_id", -1);

        Intent intent = new Intent(SelectProductScreen.this, PayScreen.class);
        intent.putExtra("area_id", areaId);
        intent.putExtra("table_id", tableId);
        startActivity(intent);
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            Toast.makeText(this, "Bạn chưa gọi món", Toast.LENGTH_SHORT).show();
            return;
        }

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (Map.Entry<ProductResponse, Integer> entry : cart.entrySet()) {
            int productId = entry.getKey().getId();
            int quantity = entry.getValue();
            orderDetails.add(new OrderDetail(productId, quantity));

        }

        int tableId = getIntent().getIntExtra("table_id", -1);
        int areaId = getIntent().getIntExtra("area_id", -1);

        OrderRequest orderRequest = new OrderRequest(tableId, orderDetails);
        getOrderId(areaId, tableId, new OnOrderIdReceivedListener() {
            @Override
            public void onOrderIdReceived(int orderId) {
                Log.d("id", String.valueOf(orderId));
                if (orderId != 0 && orderId != -1) {
                    Toast.makeText(SelectProductScreen.this, "Bàn này đã gọi món" , Toast.LENGTH_LONG).show();
                } else  {
                    OrderRequest orderRequest = new OrderRequest(tableId, orderDetails);
                    ApiService apiService = RetrofitClient.getClient(SelectProductScreen.this).create(ApiService.class);
                    apiService.postOrder(orderRequest).enqueue(new Callback<OrderResponse>() {
                        @Override
                        public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                int orderId = response.body().getOrderId();
                                updateTableData(areaId, tableId, orderId, STATUS_SERVING);
                                Toast.makeText(SelectProductScreen.this, "Gọi món thành công!", Toast.LENGTH_SHORT).show();
                                delayStartActivity(areaId);
                            } else {
                                try {
                                    String errorMessage = response.errorBody().string();
                                    Log.e(TAG, "Error placing order: " + errorMessage);
                                    Toast.makeText(SelectProductScreen.this, "Gọi món thất bại" , Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing error response", e);
                                    Toast.makeText(SelectProductScreen.this, "Gọi món thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderResponse> call, Throwable t) {

                        }
                    });
                }
            }
        });

    }


    private void updateTableData(int areaId, int tableId, int orderId, String status) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AREA_REF);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot areasSnapshot) {
                for (DataSnapshot areaSnapshot : areasSnapshot.getChildren()) {
                    Integer areaIdFromDb = areaSnapshot.child("id").getValue(Integer.class);
                    if (areaIdFromDb != null && areaIdFromDb.equals(areaId)) {
                        updateTableStatus(areaSnapshot, tableId, orderId, status);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadAreas:onCancelled", databaseError.toException());
            }
        });
    }

    private void updateTableStatus(DataSnapshot areaSnapshot, int tableId, int orderId, String status) {
        DatabaseReference tablesRef = areaSnapshot.child("tables").getRef();
        tablesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot tablesSnapshot) {
                for (DataSnapshot tableSnapshot : tablesSnapshot.getChildren()) {
                    Integer tableIdFromDb = tableSnapshot.child("id").getValue(Integer.class);
                    if (tableIdFromDb != null && tableIdFromDb.equals(tableId)) {
                        tableSnapshot.getRef().child("orderId").setValue(orderId);
                        tableSnapshot.getRef().child("status").setValue(status);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadTables:onCancelled", databaseError.toException());
            }
        });
    }

    private void delayStartActivity(int areaId) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            finish();
        }, 1000);
    }

    private void showSelectTableDialog(int areaId) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_table);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        GridView tableGridView = dialog.findViewById(R.id.tableGridView);
        List<Table> tableList = new ArrayList<>();
        TableAdapter adapter = new TableAdapter(this, R.layout.table_item, tableList);
        tableGridView.setAdapter(adapter);

        DatabaseReference areasRef = FirebaseDatabase.getInstance().getReference("areas");
        areasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    Integer id = areaSnapshot.child("id").getValue(Integer.class);
                    if (id != null && id.equals(areaId)) {
                        // Found the matching area, now fetch tables
                        DatabaseReference tablesRef = areaSnapshot.child("tables").getRef();
                        tablesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot tablesSnapshot) {
                                tableList.clear();
                                for (DataSnapshot tableSnapshot : tablesSnapshot.getChildren()) {
                                    Table table = tableSnapshot.getValue(Table.class);
                                    if (table != null) {
                                        tableList.add(table);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "loadTables:onCancelled", databaseError.toException());
                            }
                        });
                        break; // Exit loop after finding the matching area
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadAreas:onCancelled", databaseError.toException());
            }
        });

        tableGridView.setOnItemClickListener((parent, view, position, id) -> {
            Table selectedTable = (Table) parent.getItemAtPosition(position);
            int areaIdCurrentTable = getIntent().getIntExtra("area_id", -1);
            int tableIdCurrentTable = getIntent().getIntExtra("table_id", -1);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Chuyển bàn / Gộp bàn")
                    .setPositiveButton("Chuyển bàn", (dialogInterface, which) -> {
                        getOrderId(areaIdCurrentTable, tableIdCurrentTable, new OnOrderIdReceivedListener() {
                            @Override
                            public void onOrderIdReceived(int orderId) {
                                if (orderId != -1) {
                                    handleTableSelectionChuyenBan(orderId, areaId, selectedTable.getId());

                                    // Introduce a delay of 2 seconds before starting the new activity
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                      finish();
                                    }, 1000); // 2000 milliseconds = 2 seconds
                                } else {
                                    Toast.makeText(SelectProductScreen.this, "Chuyển bàn không thành công. Xin thử lại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    })
                    .setNegativeButton("Gộp bàn", (dialogInterface, which) -> {
                        getOrderId(areaIdCurrentTable, tableIdCurrentTable, new OnOrderIdReceivedListener() {
                            @Override
                            public void onOrderIdReceived(int orderId) {
                                if (orderId != -1) {
                                    handleTableSelectionGopBan(orderId, areaId, selectedTable.getId());

                                    // Introduce a delay of 2 seconds before starting the new activity
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        finish();
                                    }, 1000); // 2000 milliseconds = 2 seconds
                                } else {
                                    Toast.makeText(SelectProductScreen.this, "Gộp bàn không thành công. Xin thử lại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    })
                    .setNeutralButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                    .create()
                    .show();

            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateCurrentTableStatus() {
        int areaIdCurrentTable = getIntent().getIntExtra("area_id", -1);
        int tableIdCurrentTable = getIntent().getIntExtra("table_id", -1);
        DatabaseReference areasRef = FirebaseDatabase.getInstance().getReference("areas");

        areasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot areasSnapshot) {
                for (DataSnapshot areaSnapshot : areasSnapshot.getChildren()) {
                    Integer areaIdFromDb = areaSnapshot.child("id").getValue(Integer.class);
                    if (areaIdFromDb != null && areaIdFromDb.equals(areaIdCurrentTable)) {
                        for (DataSnapshot tableSnapshot : areaSnapshot.child("tables").getChildren()) {
                            Integer tableIdFromDb = tableSnapshot.child("id").getValue(Integer.class);
                            if (tableIdFromDb != null && tableIdFromDb.equals(tableIdCurrentTable)) {
                                tableSnapshot.getRef().child("orderId").setValue(0);
                                tableSnapshot.getRef().child("status").setValue("ACTIVE");
                            }
                        }

                        return;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Failed to handle table selection", databaseError.toException());
            }
        });
    }

    private void handleTableSelectionChuyenBan(int orderId, int areaId, int tableId) {
        DatabaseReference areasRef = FirebaseDatabase.getInstance().getReference("areas");
        int tableIdCurrentTable = getIntent().getIntExtra("table_id", -1);

        areasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot areasSnapshot) {
                for (DataSnapshot areaSnapshot : areasSnapshot.getChildren()) {
                    Integer areaIdFromDb = areaSnapshot.child("id").getValue(Integer.class);
                    if (areaIdFromDb != null && areaIdFromDb.equals(areaId)) {
                        for (DataSnapshot tableSnapshot : areaSnapshot.child("tables").getChildren()) {
                            Integer tableIdFromDb = tableSnapshot.child("id").getValue(Integer.class);
                            if (tableIdFromDb != null && tableIdFromDb.equals(tableId)) {
                                if (tableIdFromDb.equals(tableIdCurrentTable)) {
                                    Toast.makeText(SelectProductScreen.this, "Không thể chuyển bàn này", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Integer orderIdFromDb = tableSnapshot.child("orderId").getValue(Integer.class);
                                if (orderIdFromDb == null || orderIdFromDb == 0) {
                                    // Update orderId and status
                                    tableSnapshot.getRef().child("orderId").setValue(orderId);
                                    tableSnapshot.getRef().child("status").setValue("SERVING");
                                    updateCurrentTableStatus();
                                } else {
                                    // Show message
                                    Toast.makeText(SelectProductScreen.this, "Bàn này đã order không thể chuyển bàn", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                        }
                        // Table not found
                        Toast.makeText(SelectProductScreen.this, "Chuyển bàn không thành công.Xin thử lại!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // Area not found
                Toast.makeText(SelectProductScreen.this, "Chuyển bàn không thành công.Xin thử lại!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to handle table selection", databaseError.toException());
            }
        });
    }
    private void handleTableSelectionGopBan(int orderId, int areaId, int tableId) {
        DatabaseReference areasRef = FirebaseDatabase.getInstance().getReference("areas");
        int tableIdCurrentTable = getIntent().getIntExtra("table_id", -1);

        areasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot areasSnapshot) {
                for (DataSnapshot areaSnapshot : areasSnapshot.getChildren()) {
                    Integer areaIdFromDb = areaSnapshot.child("id").getValue(Integer.class);
                    if (areaIdFromDb != null && areaIdFromDb.equals(areaId)) {
                        for (DataSnapshot tableSnapshot : areaSnapshot.child("tables").getChildren()) {
                            Integer tableIdFromDb = tableSnapshot.child("id").getValue(Integer.class);
                            if (tableIdFromDb != null && tableIdFromDb.equals(tableId)) {
                                if (tableIdFromDb.equals(tableIdCurrentTable)) {
                                    Toast.makeText(SelectProductScreen.this, "Không thể gộp bàn này", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Integer orderIdFromDb = tableSnapshot.child("orderId").getValue(Integer.class);
                                if (orderIdFromDb == null || orderIdFromDb == 0) {
                                    // Update orderId and status
                                    tableSnapshot.getRef().child("orderId").setValue(orderId);
                                    tableSnapshot.getRef().child("status").setValue("SERVING");
                                } else {
                                    // Show message
                                    Toast.makeText(SelectProductScreen.this, "Bàn này đã order không thể gộp bàn", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                        }
                        // Table not found
                        Toast.makeText(SelectProductScreen.this, "Gộp bàn không thành công.Xin thử lại!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // Area not found
                Toast.makeText(SelectProductScreen.this, "Gộp bàn không thành công.Xin thử lại!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Gộp bàn không thành công.Xin thử lại!", databaseError.toException());
            }
        });
    }

    private void showSelectAreaDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_area);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference areasRef = database.getReference("areas");

        List<AreaResponse> areaList = new ArrayList<>();
        AreaAdapter adapter = new AreaAdapter(this, R.layout.area_item, areaList);

        GridView areaGridView = dialog.findViewById(R.id.areaGridView);
        areaGridView.setAdapter(adapter);

        areasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<AreaResponse> areaResponses = new ArrayList<>();
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        AreaResponse area = areaSnapshot.getValue(AreaResponse.class);
                        areaResponses.add(area);
                    }
                    adapter.clear();
                    adapter.addAll(areaResponses);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        });

        // Set the onItemClick listener for the GridView
        areaGridView.setOnItemClickListener((parent, view, position, id) -> {
            AreaResponse selectedArea = (AreaResponse) parent.getItemAtPosition(position);
            dialog.dismiss();
            // Pass the selected area's ID to the showSelectTableDialog method
            showSelectTableDialog(selectedArea.getId());
        });

        dialog.show();
    }


}
