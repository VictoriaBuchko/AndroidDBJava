package com.example.myapplicationproductbd.model;
public class ShoppingList{
    private long id;
    private String name;
    private long date;
    private String description;
    private int totalCount;
    private int checkedCount;

    public ShoppingList(long id, String name, long date, String description) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public long getId(){ return id; }
    public String getName(){ return name; }
    public long getDate(){ return date; }
    public String getDescription(){ return description; }
    public int getTotalCount(){ return totalCount; }
    public int getCheckedCount(){ return checkedCount; }
    public void setTotalCount(int totalCount){ this.totalCount = totalCount; }
    public void setCheckedCount(int checkedCount) { this.checkedCount = checkedCount; }

    @Override
    public String toString() { return name; }
}