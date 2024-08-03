package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee.R;
import com.example.model.BillItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BillItemAdapter extends RecyclerView.Adapter<BillItemAdapter.ViewHolder> {
    private List<BillItem> billItems;

    public BillItemAdapter(List<BillItem> billItems) {
        this.billItems = billItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillItem item = billItems.get(position);
        holder.tvProductName.setText(item.getProductName());
        holder.tvQuantity.setText(item.getQuantity());

        // Format prices for display
        holder.tvUnitPrice.setText(formatPriceToVND(Double.parseDouble(item.getUnitPrice())));
        holder.tvTotalPrice.setText(formatPriceToVND(Double.parseDouble(item.getTotalPrice())));
    }

    @Override
    public int getItemCount() {
        return billItems.size();
    }

    private String formatPriceToVND(double price) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(price);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvQuantity;
        TextView tvUnitPrice;
        TextView tvTotalPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}
