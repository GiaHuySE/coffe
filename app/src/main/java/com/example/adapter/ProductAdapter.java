// ProductAdapter.java
package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee.R;
import com.example.model.ProductResponse;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductResponse> products;
    private CartListener cartListener;

    public ProductAdapter(List<ProductResponse> products, CartListener cartListener) {
        this.products = products;
        this.cartListener = cartListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductResponse product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface CartListener {
        void onAddToCart(ProductResponse product, int quantity);
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productName, productPrice, productAmount;
        private ImageView productImage;
        private Button btnAdd;
        private ImageView btnMinus, btnPlus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productAmount = itemView.findViewById(R.id.productAmount);

            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);

            btnAdd.setOnClickListener(v -> {
                int quantity = Integer.parseInt(productAmount.getText().toString());
                cartListener.onAddToCart(products.get(getAdapterPosition()), quantity);
            });

            btnPlus.setOnClickListener(v -> {
                int quantity = Integer.parseInt(productAmount.getText().toString());
                quantity++;
                setQuantity(quantity);
            });

            btnMinus.setOnClickListener(v -> {
                int quantity = Integer.parseInt(productAmount.getText().toString());
                if (quantity > 0) {
                    quantity--;
                    setQuantity(quantity);
                }
            });
        }

        public void bind(ProductResponse product) {
            productName.setText(product.getName());
            productPrice.setText(formatPriceToVND(product.getPrice()));
            // Set product image (use an image loading library if needed)
            // Glide.with(productImage.getContext()).load(product.getImageUrl()).into(productImage);
            setQuantity(0); // Initialize quantity display
        }

        public void setQuantity(int quantity) {
            productAmount.setText(String.valueOf(quantity));
        }

        private String formatPriceToVND(String price) {
            double priceDouble = Double.parseDouble(price);
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return format.format(priceDouble);
        }
    }
}
