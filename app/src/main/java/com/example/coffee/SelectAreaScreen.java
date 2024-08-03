package com.example.coffee;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adapter.AreaAdapter;
import com.example.api.ApiService;
import com.example.api.RetrofitClient;
import com.example.model.AreaResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectAreaScreen extends AppCompatActivity {

    private GridView gridView;
    private AreaAdapter adapter;
    private ImageView imageHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area_screen);

        gridView = findViewById(R.id.gridView);
        imageHeader = findViewById(R.id.header_icon);

        // Initialize adapter with an empty list
        adapter = new AreaAdapter(this, R.layout.area_item, new ArrayList<>());
        gridView.setAdapter(adapter);

        // Initialize Firebase database reference
       FirebaseDatabase.getInstance().getReference("areas");

        imageHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectAreaScreen.this, OrderScreen.class);
                startActivity(intent);
            }
        });

        // Fetch areas from the API
        fetchAreas();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AreaResponse selectedArea = (AreaResponse) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(SelectAreaScreen.this, SelectTableScreen.class);
                intent.putExtra("area_name", selectedArea.getName());
                intent.putExtra("area_id", selectedArea.getId());
//                intent.putParcelableArrayListExtra("tables", new ArrayList<>(selectedArea.getTables()));
                startActivity(intent);
            }
        });
    }

    private void fetchAreas() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference areasRef = database.getReference("areas");

        // Read from Firebase to check if data exists
        areasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Data exists in Firebase, use it to populate the GridView
                    List<AreaResponse> areaResponses = new ArrayList<>();
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        AreaResponse area = areaSnapshot.getValue(AreaResponse.class);
                        areaResponses.add(area);
                    }
                    adapter.clear();
                    adapter.addAll(areaResponses);
                    adapter.notifyDataSetChanged();

                } else {
                    // Data does not exist in Firebase, fetch from API
                    fetchAreasFromApiAndSaveToFirebase(areasRef);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(SelectAreaScreen.this, "Không tìm thấy khu vực: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAreasFromApiAndSaveToFirebase(DatabaseReference areasRef) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<List<AreaResponse>> call = apiService.getAreas();
        call.enqueue(new Callback<List<AreaResponse>>() {
            @Override
            public void onResponse(Call<List<AreaResponse>> call, Response<List<AreaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AreaResponse> areaResponses = response.body();
                    adapter.clear();
                    adapter.addAll(areaResponses);
                    adapter.notifyDataSetChanged();

                    Map<String, Object> areaUpdates = new HashMap<>();
                    for (AreaResponse area : areaResponses) {
                        areaUpdates.put(String.valueOf(area.getId()), area);
                    }

                    // Write new data to Firebase
                    areasRef.updateChildren(areaUpdates).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    });

                } else {
                    // Handle errors or empty response
                    Toast.makeText(SelectAreaScreen.this, "Không tìm thấy khu vực:", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AreaResponse>> call, Throwable t) {
                // Handle network failure
                Toast.makeText(SelectAreaScreen.this, "Network failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
