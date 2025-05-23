//databasehelper.java


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


//model.java

package com.example.lostfoundmap.model;

public class Item {
    private String title;
    private double latitude;
    private double longitude;

    public Item(String title, double latitude, double longitude) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

//additemactivity.java

package com.example.lostfoundmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.lostfoundmap.database.DatabaseHelper;
import com.example.lostfoundmap.model.Item;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.gms.common.api.Status;

import java.util.*;

public class AddItemActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private RadioGroup postTypeGroup;
    private EditText nameInput, phoneInput, descInput, dateInput, locationInput;
    private Button currentLocationButton;

    private double lat = 0.0, lon = 0.0;
    private FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        postTypeGroup = findViewById(R.id.postTypeGroup);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        descInput = findViewById(R.id.descInput);
        dateInput = findViewById(R.id.dateInput);
        locationInput = findViewById(R.id.locationInput);
        currentLocationButton = findViewById(R.id.currentLocationButton);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAhnu7twGpHBlnblUliYAbYCXbsWk98AP4"); // Replace with your API key
        }

        locationInput.setOnClickListener(v -> openPlaceAutocomplete());
        currentLocationButton.setOnClickListener(v -> getCurrentLocation());
    }

    public void saveItem(View view) {
        String postType = ((RadioButton) findViewById(postTypeGroup.getCheckedRadioButtonId())).getText().toString();
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String description = descInput.getText().toString();
        String date = dateInput.getText().toString();
        String location = locationInput.getText().toString();

        DatabaseHelper db = new DatabaseHelper(this);
        db.insertItem(postType, name, phone, description, date, location, lat, lon);

        Toast.makeText(this, "Item saved to DB!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void openPlaceAutocomplete() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                locationInput.setText("Lat: " + lat + ", Lon: " + lon);
                Toast.makeText(this, "Location retrieved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                locationInput.setText(place.getAddress());
                if (place.getLatLng() != null) {
                    lat = place.getLatLng().latitude;
                    lon = place.getLatLng().longitude;
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Place error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

//allitemactivity.jaav

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

//itemadapter.java

package com.example.lostfoundmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lostfoundmap.model.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final List<Item> itemList;

    public ItemAdapter(List<Item> items) {
        this.itemList = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, latlngText;

        public ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.titleText);
            latlngText = view.findViewById(R.id.latlngText);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.titleText.setText(item.getTitle());
        holder.latlngText.setText("Lat: " + item.getLatitude() + ", Lng: " + item.getLongitude());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}


//mainactivity.java

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
//mapactivity.java

package com.example.lostfoundmap;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.example.lostfoundmap.model.Item;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // This will be set from MainActivity
    public static List<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (itemList == null || itemList.isEmpty()) {
            return;
        }

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (Item item : itemList) {
            LatLng position = new LatLng(item.getLatitude(), item.getLongitude());
            mMap.addMarker(new MarkerOptions().position(position).title(item.getTitle()));
            boundsBuilder.include(position);
        }

        // Animate the camera to show all markers
        mMap.setOnMapLoadedCallback(() -> {
            LatLngBounds bounds = boundsBuilder.build();
            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        });
    }
}
