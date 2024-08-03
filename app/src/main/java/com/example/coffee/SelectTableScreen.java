//package com.example.coffee;
//
//import static android.content.ContentValues.TAG;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.TextView;
//
//import com.example.adapter.TableAdapter;
//import com.example.model.AreaResponse;
//import com.example.model.Table;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//
//public class SelectTableScreen extends AppCompatActivity {
//
//    private GridView gridView;
//    private TableAdapter adapter;
//    private TextView header;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_select_table_screen);
//        header = findViewById(R.id.header);
//        gridView = findViewById(R.id.gridView);
//
//        Intent intent = getIntent();
//        String areaName = intent.getStringExtra("area_name");
//        adapter = new TableAdapter(this, R.layout.table_item, tableList);
//        gridView.setAdapter(adapter);
//        header.setText(areaName);
////        databaseReference = FirebaseDatabase.getInstance().getReference("areas");
////        databaseReference.child(String.valueOf(areaId)).addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                String retrievedAreaName = dataSnapshot.child("name").getValue(String.class);
////                Log.d("Test",retrievedAreaName);
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////                Log.w(TAG, "loadAreaName:onCancelled", databaseError.toException());
////                header.setText(areaName); // Use the passed area name
////            }
////        });
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Table selectedTable = (Table) adapterView.getItemAtPosition(position);
//                Intent productIntent = new Intent(SelectTableScreen.this, SelectProductScreen.class);
//                productIntent.putExtra("table_name", selectedTable.getName());
//                int areaId = getIntent().getIntExtra("area_id", -1);
//                productIntent.putExtra("area_id",areaId );
//                productIntent.putExtra("table_id",selectedTable.getId());
//
//                startActivity(productIntent);
//            }
//        });
//    }
//}
package com.example.coffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.adapter.TableAdapter;
import com.example.model.Table;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectTableScreen extends AppCompatActivity {

    private GridView gridView;
    private TableAdapter adapter;
    private TextView header;
    private List<Table> tableList = new ArrayList<>();
    private Button btnBack;
    private DatabaseReference databaseReference;
    private static final String TAG = "SelectTableScreen";

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        int areaId = intent.getIntExtra("area_id", -1);
        fetchTables(areaId);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_table_screen);

        header = findViewById(R.id.header);
        gridView = findViewById(R.id.gridView);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Get area ID and name from intent
        Intent intent = getIntent();
        int areaId = intent.getIntExtra("area_id", -1);

        // Initialize TableAdapter with an empty list
        adapter = new TableAdapter(this, R.layout.table_item, tableList);
        gridView.setAdapter(adapter);

        // Set header text
        header.setText("Khu vực");

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("areas").child(String.valueOf(areaId)).child("tables");

        // Fetch tables from Firebase
        fetchTables(areaId);

        // Handle item clicks
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Table selectedTable = (Table) adapterView.getItemAtPosition(position);
                Intent productIntent = new Intent(SelectTableScreen.this, SelectProductScreen.class);
                productIntent.putExtra("table_name", selectedTable.getName());
                productIntent.putExtra("area_id", areaId);
                productIntent.putExtra("table_id", selectedTable.getId());
                startActivity(productIntent);
            }
        });
    }

private void fetchTables(int areaId) {
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
                            Log.w(TAG, "loadTables:onCancelled", databaseError.toException());
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
}
}