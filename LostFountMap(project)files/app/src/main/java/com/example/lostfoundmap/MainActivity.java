package com.example.lostfoundmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfoundmap.database.DatabaseHelper;
import com.example.lostfoundmap.model.Item;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onAddItem(View view) {
        startActivity(new Intent(this, AddItemActivity.class));
    }

    public void onShowMap(View view) {
        // Fetch items from database
        DatabaseHelper db = new DatabaseHelper(this);
        List<Item> items = db.getAllItems();

        // Pass to MapActivity using static list
        MapActivity.itemList = items;

        // Launch map activity
        startActivity(new Intent(this, MapActivity.class));
    }

    public void onShowAllItems(View view) {
        startActivity(new Intent(this, AllItemsActivity.class)); // Create this screen next
    }
}
