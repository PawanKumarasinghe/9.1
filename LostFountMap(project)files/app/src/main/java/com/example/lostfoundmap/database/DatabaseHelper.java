package com.example.lostfoundmap.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lostfoundmap.model.Item;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "lost_found.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "LostFoundItems";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "postType TEXT, " +
                "name TEXT, " +
                "phone TEXT, " +
                "description TEXT, " +
                "date TEXT, " +
                "location TEXT, " +
                "latitude REAL, " +
                "longitude REAL)";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void insertItem(String postType, String name, String phone, String desc, String date, String location, double lat, double lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + TABLE_NAME + " (postType, name, phone, description, date, location, latitude, longitude) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        db.execSQL(query, new Object[]{postType, name, phone, desc, date, location, lat, lon});
        db.close();
    }
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("postType")) + ": " +
                        cursor.getString(cursor.getColumnIndexOrThrow("name")) + " - " +
                        cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double lon = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

                items.add(new Item(title, lat, lon));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

}
