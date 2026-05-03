package com.example.myapplicationproductbd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationproductbd.adapter.ProductAdapter;
import com.example.myapplicationproductbd.db.DatabaseManager;
import com.example.myapplicationproductbd.model.Product;
import com.example.myapplicationproductbd.model.ShoppingList;
import com.example.myapplicationproductbd.model.Type;

import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCT_ID = "product_id";
    private long listId;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> products;
    private List<Type> types;
    private TextView tvTitle, tvCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        tvTitle = findViewById(R.id.tvProductsTitle);
        tvCounter = findViewById(R.id.tvProductsCounter);
        recyclerView = findViewById(R.id.recyclerProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listId = getIntent().getLongExtra(MainActivity.EXTRA_LIST_ID, 0);

        //завантаження типів
        DatabaseManager db = new DatabaseManager(this);
        db.openR();
        types = db.getTypes();
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        DatabaseManager db = new DatabaseManager(this);
        db.openR();
        products = db.getProducts(listId);
        ShoppingList list = db.getList(listId);
        db.close();

        if (list != null) {
            tvTitle.setText(list.getName());
            tvCounter.setText("Куплено: " + list.getCheckedCount() + " / " + list.getTotalCount());
        }

        if (adapter == null) {
            adapter = new ProductAdapter(this, products, types, new ProductAdapter.OnProductClickListener() {
                @Override
                public void onProductClick(Product product) {
                    openProductForm(product.getId());
                }

                @Override
                public void onProductLongClick(Product product) {
                    new AlertDialog.Builder(ProductsActivity.this)
                            .setTitle(product.getName())
                            .setItems(new String[]{"Редагувати", "Видалити"}, (dialog, which) -> {
                                if (which == 0) {
                                    openProductForm(product.getId());
                                } else {
                                    confirmDeleteProduct(product);
                                }
                            })
                            .show();
                }

                @Override
                public void onCheckedChange(Product product, boolean isChecked) {
                    DatabaseManager db2 = new DatabaseManager(ProductsActivity.this);
                    db2.openW();
                    db2.toggleChecked(product.getId(), isChecked ? 1 : 0);
                    db2.close();
                    loadProducts(); //оновлення лічильника
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(products);
        }
    }

    private void openProductForm(long productId) {
        Intent intent = new Intent(this, ProductFormActivity.class);
        intent.putExtra(MainActivity.EXTRA_LIST_ID, listId);
        if (productId > 0) intent.putExtra(EXTRA_PRODUCT_ID, productId);
        startActivity(intent);
    }

    private void confirmDeleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Видалити покупку?")
                .setMessage("\"" + product.getName() + "\" буде видалено.")
                .setPositiveButton("Видалити", (d, w) -> {
                    DatabaseManager db = new DatabaseManager(this);
                    db.openW();
                    db.deleteProduct(product.getId());
                    db.close();
                    loadProducts();
                })
                .setNegativeButton("Скасувати", null)
                .show();
    }

    public void addProduct(View view) {
        openProductForm(0);
    }

    public void goBack(View view) {
        finish();
    }
}