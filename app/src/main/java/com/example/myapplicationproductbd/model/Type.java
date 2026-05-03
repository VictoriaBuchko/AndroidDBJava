package com.example.myapplicationproductbd.model;

public class Type {
    private long id;
    private String label;
    private String rule;

    public Type(long id, String label, String rule) {
        this.id = id;
        this.label = label;
        this.rule = rule;
    }

    public long getId(){ return id; }
    public String getLabel(){ return label; }

    @Override
    public String toString(){ return label; }
}