package com.palungo.coffee.pojo;

/**
 * Created by Sanjay on 6/27/2016.
 */
public class SalesInfo {
    private int id;
    private String date;
    private int qty;
    private int price;
    private String name;

    public SalesInfo(int id, String date, int qty, int price, String name) {
        this.id = id;
        this.date = date;
        this.qty = qty;
        this.price = price;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getQty() {
        return qty;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
