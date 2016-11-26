package com.palungo.coffee.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.getbase.android.schema.Schemas;
import com.google.common.collect.ImmutableList;
import com.palungo.coffee.GlobalApplication;
import com.palungo.coffee.pojo.InventoryInfo;
import com.palungo.coffee.pojo.ItemInfo;
import com.palungo.coffee.pojo.SalesInfo;

import java.util.ArrayList;

/**
 * Created by sanjay on 6/29/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance = null;
    private Context mContext;

    private static final String DB_NAME = "com.palungo.coffee";
    private static final int DB_VERSION = 1;
    private static final int TABLE_REVISION = 1;


    private class Tables {
        private static final String TABLE_ITEM = "items";
        private static final String TABLE_INVENTORY = "inventory";
        private static final String TABLE_SALES = "sales";
    }

    private class Item {
        private static final String ITEM_ID = "id";
        private static final String ITEM_NAME = "name";
        private static final String ITEM_PRICE = "price";
    }

    private class Inventory {
        private static final String INVENTORY_ID = "id";
        private static final String INVENTORY_DATE = "date";
        private static final String INVENTORY_QTY = "qty";
        private static final String INVENTORY_PRICE = "price";
        private static final String INVENTORY_NAME = "name";
    }

    private class Sales {
        private static final String SALES_ID = "id";
        private static final String SALES_NAME = "name";
        private static final String SALES_DATE = "date";
        private static final String SALES_QTY = "qty";
        private static final String SALES_PRICE = "price";
    }

    public static DatabaseHelper getInstance(Context c) {
        if (mInstance == null) {
            return new DatabaseHelper(c);
        }
        return mInstance;
    }

    private DatabaseHelper(Context mContext) {
        super(mContext, DB_NAME, null, DB_VERSION);
        this.mContext = mContext;
    }

    private static final Schemas SCHEMA = Schemas.Builder
            .currentSchema(TABLE_REVISION,
                    new Schemas.TableDefinition(
                            Tables.TABLE_ITEM,
                            ImmutableList.<Schemas.TableDefinitionOperation>builder()
                                    .add(
                                            new Schemas.AddColumn(Item.ITEM_ID, "INTEGER PRIMARY KEY AUTOINCREMENT"),
                                            new Schemas.AddColumn(Item.ITEM_NAME, "VARCHAR(50) NOT NULL"),
                                            new Schemas.AddColumn(Item.ITEM_PRICE, "INTEGER NOT NULL")
                                    )
                                    .build()
                    ),
                    new Schemas.TableDefinition(
                            Tables.TABLE_INVENTORY,
                            ImmutableList.<Schemas.TableDefinitionOperation>builder()
                                    .add(
                                            new Schemas.AddColumn(Inventory.INVENTORY_ID, "INTEGER PRIMARY KEY AUTOINCREMENT"),
                                            new Schemas.AddColumn(Inventory.INVENTORY_DATE, "VARCHAR(50) NOT NULL"),
                                            new Schemas.AddColumn(Inventory.INVENTORY_QTY, "INTEGER NOT NULL"),
                                            new Schemas.AddColumn(Inventory.INVENTORY_PRICE, "INTEGER NOT NULL"),
                                            new Schemas.AddColumn(Inventory.INVENTORY_NAME, "VARCHAR(50) NOT NULL")
                                    )
                                    .build()
                    ),
                    new Schemas.TableDefinition(
                            Tables.TABLE_SALES,
                            ImmutableList.<Schemas.TableDefinitionOperation>builder()
                                    .add(
                                            new Schemas.AddColumn(Sales.SALES_ID, "INTEGER PRIMARY KEY AUTOINCREMENT"),
                                            new Schemas.AddColumn(Sales.SALES_DATE, "VARCHAR(50) NOT NULL"),
                                            new Schemas.AddColumn(Sales.SALES_QTY, "INTEGER NOT NULL"),
                                            new Schemas.AddColumn(Sales.SALES_PRICE, "INTEGER NOT NULL"),
                                            new Schemas.AddColumn(Sales.SALES_NAME, "VARCHAR(50) NOT NULL")
                                    )
                                    .build()
                    )
            ).build();

    @Override
    public void onCreate(SQLiteDatabase db) {
        Schemas.Schema currentSchema = SCHEMA.getCurrentSchema();
        for (String table : currentSchema.getTables()) {
            db.execSQL(currentSchema.getCreateTableStatement(table));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SCHEMA.upgrade(mContext, db, oldVersion, newVersion);
    }

    public void addItems(String name, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Item.ITEM_NAME, name);
        v.put(Item.ITEM_PRICE, price);
        db.insert(Tables.TABLE_ITEM, null, v);
        db.close();
    }

    public ArrayList<ItemInfo> getItems() {
        ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
        String query = "SELECT * FROM " + Tables.TABLE_ITEM;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                mItemInfoList.add(new ItemInfo(c.getInt(0), c.getString(1), c.getInt(2)));
            } while (c.moveToNext());
        }
        c.close();
        return mItemInfoList;
    }

    public void updateItems(int id, String name, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Item.ITEM_NAME, name);
        v.put(Item.ITEM_PRICE, price);
        db.update(Tables.TABLE_ITEM, v, Item.ITEM_ID + "=" + id, null);
        db.close();
    }

    public void deleteItems(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Tables.TABLE_ITEM, Item.ITEM_ID + " =?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void addInventory(String name, int price, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Inventory.INVENTORY_DATE, GlobalApplication.singleton.getDate(null, null, null));
        v.put(Inventory.INVENTORY_QTY, qty);
        v.put(Inventory.INVENTORY_NAME, name);
        v.put(Inventory.INVENTORY_PRICE, price);
        db.insert(Tables.TABLE_INVENTORY, null, v);
        db.close();
    }

    public ArrayList<InventoryInfo> getInventory(String date) {
        ArrayList<InventoryInfo> mInventoryList = new ArrayList<>();
        // InnerJoin based on the inventory id;
        String query = "SELECT * FROM " + Tables.TABLE_INVENTORY + " WHERE " + Sales.SALES_DATE + "=" + "Date('" + date + "')";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                mInventoryList.add(new InventoryInfo(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3), c.getString(4)));
            } while (c.moveToNext());
        }
        c.close();
        return mInventoryList;
    }

    public void updateInventory(int id, String name, int price, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Inventory.INVENTORY_QTY, qty);
        v.put(Inventory.INVENTORY_NAME, name);
        v.put(Inventory.INVENTORY_PRICE, price);
        db.update(Tables.TABLE_INVENTORY, v, Inventory.INVENTORY_ID + "=" + id, null);
        db.close();
    }

    public void deleteInventory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Tables.TABLE_INVENTORY, Inventory.INVENTORY_ID + " =?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void addSales(String name, int qty, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Sales.SALES_NAME, name);
        v.put(Sales.SALES_DATE, GlobalApplication.singleton.getDate(null, null, null));
        v.put(Sales.SALES_QTY, qty);
        v.put(Sales.SALES_PRICE, price);
        db.insert(Tables.TABLE_SALES, null, v);
        db.close();
    }

    public ArrayList<SalesInfo> getSales(String date) {
        ArrayList<SalesInfo> mSalesList = new ArrayList<>();
        if (date != null && !date.equals("")) {
            String query = "SELECT * FROM " + Tables.TABLE_SALES + " WHERE " + Sales.SALES_DATE + "=" + "Date('" + date + "')";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    mSalesList.add(new SalesInfo(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3), c.getString(4)));
                } while (c.moveToNext());
            }
            c.close();
        }
        return mSalesList;
    }

    public void updateSales(int id, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Sales.SALES_QTY, qty);
        db.update(Tables.TABLE_SALES, v, Sales.SALES_ID + "=" + id, null);
        db.close();
    }

    public void deleteSales(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Tables.TABLE_SALES, Sales.SALES_ID + " =?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int getTotalSales(String fromDate, String toDate) {
        int totalSales = 0;

        if (fromDate != null && !fromDate.equals("") && toDate != null && !toDate.equals("")) {
            String query = "SELECT " + Sales.SALES_PRICE + "," + Sales.SALES_QTY + " FROM " + Tables.TABLE_SALES + " WHERE " + Sales.SALES_DATE + " BETWEEN '" + fromDate + "' AND '" + toDate + "'";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    totalSales = totalSales + (c.getInt(c.getColumnIndex(Sales.SALES_QTY)) * c.getInt(c.getColumnIndex(Sales.SALES_PRICE)));
                } while (c.moveToNext());
            }
            c.close();
        }

        return totalSales;
    }

    public int getTotalInventory(String fromDate, String toDate) {
        int totalInventory = 0;
        if (fromDate != null && !fromDate.equals("") && toDate != null && !toDate.equals("")) {
            String query = "SELECT " + Inventory.INVENTORY_PRICE+ "," + Inventory.INVENTORY_QTY + " FROM " + Tables.TABLE_INVENTORY+ " WHERE " + Inventory.INVENTORY_DATE + " BETWEEN '" + fromDate + "' AND '" + toDate + "'";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    totalInventory = totalInventory + (c.getInt(c.getColumnIndex(Inventory.INVENTORY_QTY)) * c.getInt(c.getColumnIndex(Inventory.INVENTORY_PRICE)));
                } while (c.moveToNext());
            }
            c.close();
        }
        return totalInventory;
    }

}
