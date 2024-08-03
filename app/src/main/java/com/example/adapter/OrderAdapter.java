package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee.OrderScreen;
import com.example.coffee.R;
import com.example.coffee.RecipeScreen;
import com.example.model.OrderResponseDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderResponseDto> orderList;
    private Context context;

    public OrderAdapter(OrderScreen orderScreen, List<OrderResponseDto> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        context = parent.getContext();
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderResponseDto order = orderList.get(position);
        holder.orderName.setText(order.getTable().getName());
        holder.orderZone.setText("Khu: " + order.getTable().getArea().getName());

        // Format time
        String formattedTime = formatTime(order.getTimeIn());
        holder.timeOrder.setText("Thời gian: " + formattedTime);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeScreen.class);
            intent.putExtra("orderId", order.getId()); // Pass orderId
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderName, orderZone, timeOrder;
        LinearLayout ingredientLayout;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.orderName);
            orderZone = itemView.findViewById(R.id.orderZone);
            timeOrder = itemView.findViewById(R.id.timeOrder);
            ingredientLayout = itemView.findViewById(R.id.ingredientLayout);
        }
    }

    private String formatTime(String time) {
        // Define the input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        try {
            // Parse the input time string
            Date date = inputFormat.parse(time);
            if (date != null) {
                // Format the date object into a readable string
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time; // Return the original time string if parsing fails
    }
}
