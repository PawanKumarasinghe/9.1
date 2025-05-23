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
