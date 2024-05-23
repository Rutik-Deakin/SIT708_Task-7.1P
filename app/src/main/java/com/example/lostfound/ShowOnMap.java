package com.example.lostfound;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class ShowOnMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper databaseHelper;
    private Map<Marker, Integer> markerDataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_on_map);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Initialize the map
        markerDataMap = new HashMap<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add markers to the map based on database data
        addMarkersFromDatabase();
        // Calculate the center position of all markers
        LatLng center = calculateCenterPosition();

        // Move the camera to the center position
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10)); // Adjust zoom level as needed

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Retrieve data associated with the marker
                Integer dataId = markerDataMap.get(marker);
                if (dataId != null) {
                    Cursor cursor = databaseHelper.getDataById(dataId);
                    if (cursor != null && cursor.moveToFirst()) {
                        // Extract data from cursor
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String phone = cursor.getString(cursor.getColumnIndex("phone"));
                        String description = cursor.getString(cursor.getColumnIndex("description"));
                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        String location = cursor.getString(cursor.getColumnIndex("location"));
                        String postType = cursor.getString(cursor.getColumnIndex("post_type"));

                        // Create an intent to navigate to the ViewItem activity
                        Intent intent = new Intent(ShowOnMap.this, ViewItem.class);
                        intent.putExtra("id", dataId + "");
                        intent.putExtra("name", name);
                        intent.putExtra("phone", phone);
                        intent.putExtra("description", description);
                        intent.putExtra("date", date);
                        intent.putExtra("location", location);
                        intent.putExtra("post_type", postType);
                        startActivity(intent);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return false;
            }
        });
    }
    private LatLng calculateCenterPosition() {
        double totalLat = 0.0;
        double totalLng = 0.0;
        int count = 0;

        for (Marker marker : markerDataMap.keySet()) {
            LatLng position = marker.getPosition();
            totalLat += position.latitude;
            totalLng += position.longitude;
            count++;
        }

        if (count > 0) {
            double avgLat = totalLat / count;
            double avgLng = totalLng / count;
            return new LatLng(avgLat, avgLng);
        } else {
            // Default to a known location if there are no markers
            return new LatLng(37.8136, 144.9631);
        }
    }


    private void addMarkersFromDatabase() {
        Cursor cursor = databaseHelper.getAllData();
        if (cursor.moveToFirst()) {
            do {
                String locationData = cursor.getString(cursor.getColumnIndex("location"));
                String[] parts = locationData.split("\\?");
                if (parts.length == 3) {
                    String locationName = parts[0];
                    double latitude = Double.parseDouble(parts[1]);
                    double longitude = Double.parseDouble(parts[2]);
                    int dataId = cursor.getInt(cursor.getColumnIndex("id"));

                    // Create LatLng object
                    LatLng latLng = new LatLng(latitude, longitude);

                    // Add marker to map
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(locationName));

                    // Store the data id associated with this marker
                    markerDataMap.put(marker, dataId);

                    // Move the camera to the first marker position
                    if (cursor.isFirst()) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10)); // Adjust zoom level as needed
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
