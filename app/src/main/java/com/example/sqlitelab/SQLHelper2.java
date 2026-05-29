package com.example.sqlitelab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLHelper2 extends SQLiteOpenHelper {

    // Константы для базы данных
    public static final String DATABASE_NAME = "tailor_shop.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "orders";

    // Константы для названий столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CLIENT_NAME = "client_name";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_COST = "cost";
    public static final String COLUMN_STATUS = "status";

    // SQL запрос для создания таблицы
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CLIENT_NAME + " TEXT NOT NULL, "
            + COLUMN_ORDER_DATE + " TEXT NOT NULL, "
            + COLUMN_COST + " REAL NOT NULL, "
            + COLUMN_STATUS + " TEXT NOT NULL);";

    public SQLHelper2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Добавление заказа (Create)
    public long addOrder(String clientName, String orderDate, double cost, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLIENT_NAME, clientName);
        values.put(COLUMN_ORDER_DATE, orderDate);
        values.put(COLUMN_COST, cost);
        values.put(COLUMN_STATUS, status);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Получение всех заказов (Read)
    public ArrayList<Order> getAllOrders() {
        ArrayList<Order> orderList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String clientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLIENT_NAME));
                String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));
                double cost = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COST));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));

                orderList.add(new Order(id, clientName, orderDate, cost, status));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return orderList;
    }

    // Обновление заказа (Update)
    public int updateOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLIENT_NAME, order.getClientName());
        values.put(COLUMN_ORDER_DATE, order.getOrderDate());
        values.put(COLUMN_COST, order.getCost());
        values.put(COLUMN_STATUS, order.getStatus());

        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(order.getId())});
    }

    // Удаление заказа (Delete)
    public void deleteOrder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Поиск заказа по ID
    public Order getOrderById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Order order = null;
        if (cursor.moveToFirst()) {
            String clientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLIENT_NAME));
            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));
            double cost = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COST));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
            order = new Order(id, clientName, orderDate, cost, status);
        }

        cursor.close();
        db.close();
        return order;
    }
}