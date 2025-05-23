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
