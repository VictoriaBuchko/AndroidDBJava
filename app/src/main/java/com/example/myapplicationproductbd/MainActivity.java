package com.example.myapplicationproductbd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationproductbd.adapter.ListAdapter;
import com.example.myapplicationproductbd.db.DatabaseManager;
import com.example.myapplicationproductbd.model.ShoppingList;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_LIST_ID = "list_id";
    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private List<ShoppingList> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLists();
    }

    private void loadLists() {
        DatabaseManager db = new DatabaseManager(this);
        db.openR();
        lists = db.getLists();
        db.close();

        if (adapter == null) {
            adapter = new ListAdapter(this, lists, new ListAdapter.OnListClickListener() {
                @Override
                public void onListClick(ShoppingList list) {
                    //перехід на екран покупок
                    Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
                    intent.putExtra(EXTRA_LIST_ID, list.getId());
                    startActivity(intent);
                }

                @Override
                public void onListLongClick(ShoppingList list) {
                    //довгий тап редагування
                    openListForm(list.getId());
                }

                @Override
                public void onListDelete(ShoppingList list) {
                    //кнопка видалити у підтвердженны видалення
                    confirmDeleteList(list);
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(lists);
        }
    }

    public void addList(View view) {
        openListForm(0);
    }

    private void openListForm(long listId) {
        Intent intent = new Intent(this, ListFormActivity.class);
        if (listId > 0) intent.putExtra(EXTRA_LIST_ID, listId);
        startActivity(intent);
    }

    private void confirmDeleteList(ShoppingList list) {
        new AlertDialog.Builder(this)
                .setTitle("Видалити список?")
                .setMessage("\"" + list.getName() + "\" та всі його покупки будуть видалені.")
                .setPositiveButton("Видалити", (d, w) -> {
                    DatabaseManager db = new DatabaseManager(this);
                    db.openW();
                    db.deleteList(list.getId());
                    db.close();
                    loadLists();
                })
                .setNegativeButton("Скасувати", null)
                .show();
    }
}