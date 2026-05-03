package com.example.myapplicationproductbd.db;

public class MyConstants {

    public static final String DATABASE_NAME = "shopping.db";
    public static final int SCHEMA = 1;

    //Lists
    public static final String TABLE_LISTS = "lists";
    public static final String COL_LIST_ID = "_id";
    public static final String COL_LIST_NAME = "name";
    public static final String COL_LIST_DATE = "date";
    public static final String COL_LIST_DESC = "description";

    //Type
    public static final String TABLE_TYPE = "type";
    public static final String COL_TYPE_ID = "_id";
    public static final String COL_TYPE_LABEL = "label";
    public static final String COL_TYPE_RULE = "rule";

    //Product
    public static final String TABLE_PRODUCT = "product";
    public static final String COL_PROD_ID = "_id";
    public static final String COL_PROD_NAME = "name";
    public static final String COL_PROD_COUNT = "count";
    public static final String COL_PROD_LIST_ID = "list_id";
    public static final String COL_PROD_CHECKED = "checked";
    public static final String COL_PROD_COUNT_TYPE = "count_type";
}