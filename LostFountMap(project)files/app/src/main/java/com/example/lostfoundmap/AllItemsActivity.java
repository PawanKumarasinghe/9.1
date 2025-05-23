package com.example.lostfoundmap;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostfoundmap.database.DatabaseHelper;
import com.example.lostfoundmap.model.Item;

import java.util.List;

public class AllItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper db = new DatabaseHelper(this);
        List<Item> itemList = db.getAllItems();

        adapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(adapter);
    }
}
