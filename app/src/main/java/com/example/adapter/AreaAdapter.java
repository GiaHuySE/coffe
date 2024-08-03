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
import com.example.model.Area;
import com.example.model.AreaResponse;

import java.util.List;

public class AreaAdapter extends ArrayAdapter<AreaResponse> {
    private int defaultImage;

    public AreaAdapter(@NonNull Context context, int resource, @NonNull List<AreaResponse> objects) {
        super(context, resource, objects);
        defaultImage = R.drawable.interior; // Default image resource ID
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.area_item, parent, false);
        }

        AreaResponse areaResponse = getItem(position);

        if (areaResponse != null) {
            ImageView imageView = convertView.findViewById(R.id.area_image);
            TextView textView = convertView.findViewById(R.id.area_name);

            // Set default image for all items
            imageView.setImageResource(defaultImage);
            textView.setText(areaResponse.getName());
        }

        return convertView;
    }
}