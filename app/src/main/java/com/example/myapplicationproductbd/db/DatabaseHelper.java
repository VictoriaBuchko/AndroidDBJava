package com.example.myapplicationproductbd.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, MyConstants.DATABASE_NAME, null, MyConstants.SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Lists
        db.execSQL("CREATE TABLE " + MyConstants.TABLE_LISTS + " ("
                + MyConstants.COL_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MyConstants.COL_LIST_NAME + " TEXT NOT NULL, "
                + MyConstants.COL_LIST_DATE + " INTEGER NOT NULL, "
                + MyConstants.COL_LIST_DESC + " TEXT);");

        //Type
        db.execSQL("CREATE TABLE " + MyConstants.TABLE_TYPE + " ("
                + MyConstants.COL_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MyConstants.COL_TYPE_LABEL + " TEXT NOT NULL, "
                + MyConstants.COL_TYPE_RULE + " TEXT NOT NULL);");

        //Product
        db.execSQL("CREATE TABLE " + MyConstants.TABLE_PRODUCT + " ("
                + MyConstants.COL_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MyConstants.COL_PROD_NAME + " TEXT NOT NULL, "
                + MyConstants.COL_PROD_COUNT + " REAL NOT NULL, "
                + MyConstants.COL_PROD_LIST_ID + " INTEGER NOT NULL, "
                + MyConstants.COL_PROD_CHECKED + " INTEGER NOT NULL DEFAULT 0, "
                + MyConstants.COL_PROD_COUNT_TYPE + " INTEGER NOT NULL);");

        db.execSQL("INSERT INTO " + MyConstants.TABLE_TYPE
                + " (" + MyConstants.COL_TYPE_LABEL + ", " + MyConstants.COL_TYPE_RULE + ") VALUES ('шт', 'int');");
        db.execSQL("INSERT INTO " + MyConstants.TABLE_TYPE
                + " (" + MyConstants.COL_TYPE_LABEL + ", " + MyConstants.COL_TYPE_RULE + ") VALUES ('кг', 'float');");
        db.execSQL("INSERT INTO " + MyConstants.TABLE_TYPE
                + " (" + MyConstants.COL_TYPE_LABEL + ", " + MyConstants.COL_TYPE_RULE + ") VALUES ('л', 'float');");

        //список початковий
        long now = System.currentTimeMillis() / 1000;
        db.execSQL("INSERT INTO " + MyConstants.TABLE_LISTS
                + " (" + MyConstants.COL_LIST_NAME + ", " + MyConstants.COL_LIST_DATE
                + ", " + MyConstants.COL_LIST_DESC + ") VALUES ('List 1', " + now + ", 'Test list1');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MyConstants.TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + MyConstants.TABLE_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + MyConstants.TABLE_LISTS);
        onCreate(db);
    }
}