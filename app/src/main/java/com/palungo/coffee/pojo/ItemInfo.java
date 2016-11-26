package com.palungo.coffee.pojo;

/**
 * Created by Sanjay on 6/23/2016.
 */
public class ItemInfo {

    private int ItemId;
    private String ItemName;
    private int ItemPrice;

    public ItemInfo(int itemId, String itemName, int itemPrice) {
        ItemId = itemId;
        ItemName = itemName;
        ItemPrice = itemPrice;
    }

    public ItemInfo(String itemName, int itemPrice) {
        ItemName = itemName;
        ItemPrice = itemPrice;
    }

    public int getItemId() {
        return ItemId;
    }

    public String getItemName() {
        return ItemName;
    }

    public int getItemPrice() {
        return ItemPrice;
    }
}
