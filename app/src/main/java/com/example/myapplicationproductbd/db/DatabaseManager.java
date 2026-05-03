package com.example.myapplicationproductbd.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplicationproductbd.model.Product;
import com.example.myapplicationproductbd.model.ShoppingList;
import com.example.myapplicationproductbd.model.Type;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseManager openW() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public DatabaseManager openR() {
        database = dbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    //всі списки з кількістю товарів
    public List<ShoppingList> getLists() {
        List<ShoppingList> result = new ArrayList<>();
        Cursor cursor = database.query(
                MyConstants.TABLE_LISTS,
                null, null, null, null, null,
                MyConstants.COL_LIST_DATE + " DESC");

        while (cursor.moveToNext()) {
            ShoppingList list = cursorToList(cursor);
            //підрахунок товарив
            list.setTotalCount(getProductCount(list.getId(), false));
            list.setCheckedCount(getProductCount(list.getId(), true));
            result.add(list);
        }
        cursor.close();
        return result;
    }

    public ShoppingList getList(long id) {
        Cursor cursor = database.query(
                MyConstants.TABLE_LISTS,
                null,
                MyConstants.COL_LIST_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
        ShoppingList list = null;
        if (cursor.moveToFirst()) {
            list = cursorToList(cursor);
            list.setTotalCount(getProductCount(id, false));
            list.setCheckedCount(getProductCount(id, true));
        }
        cursor.close();
        return list;
    }

    public long insertList(ShoppingList list) {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.COL_LIST_NAME, list.getName());
        cv.put(MyConstants.COL_LIST_DATE, list.getDate());
        cv.put(MyConstants.COL_LIST_DESC, list.getDescription());
        return database.insert(MyConstants.TABLE_LISTS, null, cv);
    }

    public long updateList(ShoppingList list) {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.COL_LIST_NAME, list.getName());
        cv.put(MyConstants.COL_LIST_DESC, list.getDescription());
        return database.update(MyConstants.TABLE_LISTS, cv,
                MyConstants.COL_LIST_ID + "=?",
                new String[]{String.valueOf(list.getId())});
    }

    public long deleteList(long listId) {
        //спочатку видаляємо всі продукти списку
        database.delete(MyConstants.TABLE_PRODUCT,
                MyConstants.COL_PROD_LIST_ID + "=?",
                new String[]{String.valueOf(listId)});
        return database.delete(MyConstants.TABLE_LISTS,
                MyConstants.COL_LIST_ID + "=?",
                new String[]{String.valueOf(listId)});
    }

    @SuppressLint("Range")
    private ShoppingList cursorToList(Cursor cursor) {
        long id     = cursor.getLong(cursor.getColumnIndex(MyConstants.COL_LIST_ID));
        String name = cursor.getString(cursor.getColumnIndex(MyConstants.COL_LIST_NAME));
        long date   = cursor.getLong(cursor.getColumnIndex(MyConstants.COL_LIST_DATE));
        String desc = cursor.getString(cursor.getColumnIndex(MyConstants.COL_LIST_DESC));
        return new ShoppingList(id, name, date, desc);
    }




    public List<Product> getProducts(long listId) {
        List<Product> result = new ArrayList<>();
        Cursor cursor = database.query(
                MyConstants.TABLE_PRODUCT,
                null,
                MyConstants.COL_PROD_LIST_ID + "=?",
                new String[]{String.valueOf(listId)},
                null, null,
                MyConstants.COL_PROD_CHECKED + " ASC, " + MyConstants.COL_PROD_NAME + " ASC");

        while (cursor.moveToNext()) {
            result.add(cursorToProduct(cursor));
        }
        cursor.close();
        return result;
    }

    public Product getProduct(long id) {
        Cursor cursor = database.query(
                MyConstants.TABLE_PRODUCT,
                null,
                MyConstants.COL_PROD_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
        Product p = null;
        if (cursor.moveToFirst()) p = cursorToProduct(cursor);
        cursor.close();
        return p;
    }

    public long insertProduct(Product product) {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.COL_PROD_NAME, product.getName());
        cv.put(MyConstants.COL_PROD_COUNT, product.getCount());
        cv.put(MyConstants.COL_PROD_LIST_ID, product.getListId());
        cv.put(MyConstants.COL_PROD_CHECKED, product.getChecked());
        cv.put(MyConstants.COL_PROD_COUNT_TYPE, product.getCountType());
        return database.insert(MyConstants.TABLE_PRODUCT, null, cv);
    }

    public long updateProduct(Product product) {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.COL_PROD_NAME, product.getName());
        cv.put(MyConstants.COL_PROD_COUNT, product.getCount());
        cv.put(MyConstants.COL_PROD_CHECKED, product.getChecked());
        cv.put(MyConstants.COL_PROD_COUNT_TYPE, product.getCountType());
        return database.update(MyConstants.TABLE_PRODUCT, cv,
                MyConstants.COL_PROD_ID + "=?",
                new String[]{String.valueOf(product.getId())});
    }

    public long deleteProduct(long productId) {
        return database.delete(MyConstants.TABLE_PRODUCT,
                MyConstants.COL_PROD_ID + "=?",
                new String[]{String.valueOf(productId)});
    }

    public long toggleChecked(long productId, int newChecked) {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.COL_PROD_CHECKED, newChecked);
        return database.update(MyConstants.TABLE_PRODUCT, cv,
                MyConstants.COL_PROD_ID + "=?",
                new String[]{String.valueOf(productId)});
    }

    private int getProductCount(long listId, boolean onlyChecked) {
        String selection = MyConstants.COL_PROD_LIST_ID + "=?"
                + (onlyChecked ? " AND " + MyConstants.COL_PROD_CHECKED + "=1" : "");
        Cursor c = database.query(MyConstants.TABLE_PRODUCT,
                new String[]{"COUNT(*)"}, selection,
                new String[]{String.valueOf(listId)},
                null, null, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    @SuppressLint("Range")
    private Product cursorToProduct(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MyConstants.COL_PROD_ID));
        String name = cursor.getString(cursor.getColumnIndex(MyConstants.COL_PROD_NAME));
        float count = cursor.getFloat(cursor.getColumnIndex(MyConstants.COL_PROD_COUNT));
        long listId = cursor.getLong(cursor.getColumnIndex(MyConstants.COL_PROD_LIST_ID));
        int checked = cursor.getInt(cursor.getColumnIndex(MyConstants.COL_PROD_CHECKED));
        int countType = cursor.getInt(cursor.getColumnIndex(MyConstants.COL_PROD_COUNT_TYPE));
        return new Product(id, name, count, listId, checked, countType);
    }

    public List<Type> getTypes() {
        List<Type> result = new ArrayList<>();
        Cursor cursor = database.query(MyConstants.TABLE_TYPE,
                null, null, null, null, null, MyConstants.COL_TYPE_ID + " ASC");
        while (cursor.moveToNext()) {
            result.add(cursorToType(cursor));
        }
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    private Type cursorToType(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MyConstants.COL_TYPE_ID));
        String label = cursor.getString(cursor.getColumnIndex(MyConstants.COL_TYPE_LABEL));
        String rule = cursor.getString(cursor.getColumnIndex(MyConstants.COL_TYPE_RULE));
        return new Type(id, label, rule);
    }

    //всі унікальні назви продуктів з усіх списків

    public List<String> getAllProductNames() {
        List<String> names = new ArrayList<>();
        Cursor cursor = database.query(true,
                MyConstants.TABLE_PRODUCT,
                new String[]{MyConstants.COL_PROD_NAME},
                null, null, null, null,
                MyConstants.COL_PROD_NAME + " ASC", null);
        while (cursor.moveToNext()) {
            names.add(cursor.getString(0));
        }
        cursor.close();
        return names;
    }
}