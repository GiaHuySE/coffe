package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.coffee.R;
import com.example.model.Table;

import java.util.List;

public class TableAdapter extends ArrayAdapter<Table> {
    private Context mContext;
    private int mResource;
    private int mDefaultImageResource; // Resource ID for default image

    public TableAdapter(@NonNull Context context, int resource, @NonNull List<Table> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mDefaultImageResource = R.drawable.table; // Default image resource
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        Table currentTable = getItem(position);

        ImageView imageView = listItem.findViewById(R.id.imageView);
        imageView.setImageResource(mDefaultImageResource); // Set default image

        TextView textViewName = listItem.findViewById(R.id.textViewName);
        textViewName.setText(currentTable.getName());

        TextView textViewStatus = listItem.findViewById(R.id.textViewStatus);
        if ("ACTIVE".equals(currentTable.getStatus())) {
            textViewStatus.setText("Bàn Trống");
        } else {
            textViewStatus.setText("Đã order");
        }

        return listItem;
    }
}
