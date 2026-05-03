package com.example.myapplicationproductbd.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationproductbd.R;
import com.example.myapplicationproductbd.model.Product;
import com.example.myapplicationproductbd.model.Type;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onProductLongClick(Product product);
        void onCheckedChange(Product product, boolean isChecked);
    }

    private List<Product> products;
    private List<Type> types;
    private final LayoutInflater inflater;
    private final OnProductClickListener listener;

    public ProductAdapter(Context context, List<Product> products, List<Type> types, OnProductClickListener listener) {
        this.products = products;
        this.types = types;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void updateData(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        //назва з закресленням якщо куплено
        holder.tvName.setText(product.getName());
        if (product.getChecked() == 1) {
            holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        //кількість і одиниці виміру
        String typeLabel = getTypeLabel(product.getCountType());
        String countStr;
        if (product.getCount() == (int) product.getCount()) {
            countStr = (int) product.getCount() + " " + typeLabel;
        } else {
            countStr = product.getCount() + " " + typeLabel;
        }
        holder.tvCount.setText(countStr);

        //знімаємо слухача, щоб встановлення стану не спрацювало зайво
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(product.getChecked() == 1);
        holder.checkBox.setOnCheckedChangeListener((btn, isChecked) ->
                listener.onCheckedChange(product, isChecked));

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onProductLongClick(product);
            return true;
        });
    }

    private String getTypeLabel(int countTypeId) {
        for (Type t : types) {
            if (t.getId() == countTypeId) return t.getLabel();
        }
        return "";
    }

    @Override
    public int getItemCount() { return products.size(); }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;
        final TextView tvName, tvCount;

        ProductViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkProduct);
            tvName = view.findViewById(R.id.tvProductName);
            tvCount = view.findViewById(R.id.tvProductCount);
        }
    }
}