package com.example.myapplicationproductbd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationproductbd.db.DatabaseManager;
import com.example.myapplicationproductbd.model.Product;
import com.example.myapplicationproductbd.model.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ProductFormActivity extends AppCompatActivity {

    //100 популярних покупок
    private static final String[] COMMON_PRODUCTS = {
            "Молоко", "Хліб", "Яйця", "Масло", "Сир", "Кефір", "Сметана", "Йогурт",
            "Сіль", "Цукор", "Борошно", "Рис", "Гречка", "Вівсяні пластівці", "Макарони",
            "Картопля", "Морква", "Цибуля", "Часник", "Помідори", "Огірки", "Капуста",
            "Буряк", "Перець болгарський", "Кабачок", "Баклажан", "Броколі", "Салат",
            "Шпинат", "Зелень (кріп, петрушка)", "Яблука", "Банани", "Апельсини",
            "Лимони", "Груші", "Виноград", "Полуниця", "Мандарини", "Ківі", "Персики",
            "Куряче філе", "Куряча грудка", "Свинина", "Яловичина", "Фарш", "Ковбаса",
            "Сосиски", "Шинка", "Бекон", "Риба (хек, минтай)", "Лосось", "Тунець",
            "Креветки", "Олія соняшникова", "Олія оливкова", "Оцет", "Соєвий соус",
            "Кетчуп", "Майонез", "Гірчиця", "Мед", "Варення", "Шоколад", "Печиво",
            "Торт", "Мороженое", "Чай", "Кава", "Какао", "Сік", "Вода мінеральна",
            "Пиво", "Вино", "Чіпси", "Горіхи", "Сухофрукти", "Консерви (горошок)",
            "Консерви (кукурудза)", "Томатна паста", "Бульйон", "Суп (пакет)",
            "Пральний порошок", "Засіб для посуду", "Мило", "Шампунь", "Гель для душу",
            "Зубна паста", "Туалетний папір", "Серветки", "Пакети для сміття",
            "Фольга", "Харчова плівка", "Батарейки", "Свічки", "Корм для тварин",
            "Дитяче харчування", "Памперси", "Вологі серветки"
    };

    private AutoCompleteTextView etProductName;
    private EditText etProductCount;
    private Spinner spinnerType;
    private Button btnSaveProduct, btnDeleteProduct, btnCancelProduct;
    private DatabaseManager db;
    private long listId = 0;
    private long productId = 0;
    private List<Type> types;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_form);

        etProductName = findViewById(R.id.etProductName);
        etProductCount = findViewById(R.id.etProductCount);
        spinnerType = findViewById(R.id.spinnerType);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);
        btnCancelProduct = findViewById(R.id.btnCancelProduct);

        db = new DatabaseManager(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listId    = extras.getLong(MainActivity.EXTRA_LIST_ID, 0);
            productId = extras.getLong(ProductsActivity.EXTRA_PRODUCT_ID, 0);
        }

        //типи для спіннера
        db.openR();
        types = db.getTypes();
        List<String> dbNames = db.getAllProductNames();
        db.close();

        //одиницф виміру для спінера
        ArrayAdapter<Type> typeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        //100 зашитих + унікальні
        Set<String> nameSet = new LinkedHashSet<>(Arrays.asList(COMMON_PRODUCTS));
        nameSet.addAll(dbNames);
        List<String> allNames = new ArrayList<>(nameSet);

        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, allNames);
        etProductName.setAdapter(nameAdapter);

        if (productId > 0) {
            //редагування
            db.openR();
            Product product = db.getProduct(productId);
            db.close();

            if (product != null) {
                etProductName.setText(product.getName());
                etProductCount.setText(String.valueOf(product.getCount()));
                for (int i = 0; i < types.size(); i++) {
                    if (types.get(i).getId() == product.getCountType()) {
                        spinnerType.setSelection(i);
                        break;
                    }
                }
            }
            btnSaveProduct.setText(R.string.btn_save);
            btnDeleteProduct.setVisibility(View.VISIBLE);
        } else {
            //додавання
            btnDeleteProduct.setVisibility(View.GONE);
        }
    }

    public void save(View view) {
        String name = etProductName.getText().toString().trim();
        String countStr = etProductCount.getText().toString().trim();

        if (name.isEmpty()) {
            etProductName.setError("Введіть назву покупки");
            return;
        }
        if (countStr.isEmpty()) {
            etProductCount.setError("Введіть кількість");
            return;
        }

        float count = Float.parseFloat(countStr);
        Type selectedType = (Type) spinnerType.getSelectedItem();
        int countType = (int) selectedType.getId();

        Product product = new Product(productId, name, count, listId, 0, countType);

        db.openW();
        if (productId > 0) {
            db.updateProduct(product);
        } else {
            db.insertProduct(product);
        }
        db.close();
        goBack();
    }

    public void delete(View view) {
        db.openW();
        db.deleteProduct(productId);
        db.close();
        goBack();
    }

    public void cancel(View view) {
        goBack();
    }

    private void goBack() {
        Intent intent = new Intent(this, ProductsActivity.class);
        intent.putExtra(MainActivity.EXTRA_LIST_ID, listId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}