package com.example.myapplicationproductbd.model;

public class Product {
    private long id;
    private String name;
    private float count;
    private long listId;
    private int checked;//0 або 1
    private int countType;

    public Product(long id, String name, float count, long listId, int checked, int countType) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.listId = listId;
        this.checked = checked;
        this.countType = countType;
    }

    public long getId(){ return id; }
    public String getName(){ return name; }
    public float getCount(){ return count; }
    public long getListId(){ return listId; }
    public int getChecked(){ return checked; }
    public int getCountType(){ return countType; }

    @Override
    public String toString() { return name; }
}