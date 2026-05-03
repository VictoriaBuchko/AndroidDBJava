package com.example.myapplicationproductbd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationproductbd.db.DatabaseManager;
import com.example.myapplicationproductbd.model.ShoppingList;

public class ListFormActivity extends AppCompatActivity {

    private EditText etName, etDesc;
    private Button btnSave, btnDelete, btnCancel;
    private DatabaseManager db;
    private long listId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_form);

        etName = findViewById(R.id.etListName);
        etDesc = findViewById(R.id.etListDesc);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);

        db = new DatabaseManager(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listId = extras.getLong(MainActivity.EXTRA_LIST_ID, 0);
        }

        if (listId > 0) {
            //редагування
            db.openR();
            ShoppingList list = db.getList(listId);
            db.close();

            if (list != null) {
                etName.setText(list.getName());
                etDesc.setText(list.getDescription() != null ? list.getDescription() : "");
            }
            btnSave.setText("Зберегти");
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            //створення
            btnDelete.setVisibility(View.GONE);
        }
    }

    public void save(View view) {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Введіть назву списку");
            return;
        }

        db.openW();
        if (listId > 0) {
            ShoppingList updated = new ShoppingList(listId, name,
                    System.currentTimeMillis() / 1000,
                    desc.isEmpty() ? null : desc);
            db.updateList(updated);
        } else {
            ShoppingList newList = new ShoppingList(0, name,
                    System.currentTimeMillis() / 1000,
                    desc.isEmpty() ? null : desc);
            db.insertList(newList);
        }
        db.close();
        goHome();
    }

    public void delete(View view) {
        db.openW();
        db.deleteList(listId);
        db.close();
        Toast.makeText(this, "Список видалено", Toast.LENGTH_SHORT).show();
        goHome();
    }

    public void cancel(View view) {
        goHome();
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}