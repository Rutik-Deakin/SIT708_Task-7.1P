package com.example.lostfound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class CreateAdvert extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private EditText editName, editPhone, editDescription, editDate, editLocation;
    private Button btnSave, btnGetCurrentLocation;
    private RadioGroup radioPostType;
    private FusedLocationProviderClient fusedLocationClient;
    private String finalLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        // Initialize Google Places API
        Places.initialize(getApplicationContext(), "AIzaSyBnSyS0XXsS9HqTcvPxJOKLnl4L_jWvgog");
        PlacesClient placesClient = Places.createClient(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        radioPostType = findViewById(R.id.radioPostType);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editDescription = findViewById(R.id.editDescription);
        editDate = findViewById(R.id.editDate);
        editLocation = findViewById(R.id.editLocation);
        btnSave = findViewById(R.id.btnSave);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);

        // Set click listener for location EditText to show autocomplete
        editLocation.setFocusable(false);
        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(CreateAdvert.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        // Set click listener for Get Current Location button
        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRadioButtonId = radioPostType.getCheckedRadioButtonId();
                String selectedPostType = "";
                if (selectedRadioButtonId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                    selectedPostType = selectedRadioButton.getText().toString();
                }
                String name = editName.getText().toString().trim();
                String phone = editPhone.getText().toString().trim();
                String desc = editDescription.getText().toString().trim();
                String date = editDate.getText().toString().trim();
                String location = editLocation.getText().toString().trim();

                if (selectedPostType.isEmpty() || name.isEmpty() || phone.isEmpty() || desc.isEmpty() || date.isEmpty() || location.isEmpty()) {
                    createToast(CreateAdvert.this, "Fill all details").show();
                    return;
                }
                createAdvert(selectedPostType, name, phone, desc, date, finalLocation);
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    editLocation.setText(location.getLatitude() + ", " + location.getLongitude());
                } else {
                    createToast(CreateAdvert.this, "Unable to fetch current location").show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                createToast(this, "Permission denied").show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
//                Log.d("TEST::::::::::", place.getName() + " " + place.getLatLng());
                finalLocation = place.getName() + "?" + place.getLatLng().latitude + "?" + place.getLatLng().longitude;
                editLocation.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                com.google.android.gms.common.api.Status status = Autocomplete.getStatusFromIntent(data);
                createToast(this, status.getStatusMessage()).show();
            }
        }
    }

    private void createAdvert(String type, String name, String phone, String desc, String date, String location) {
        DatabaseHelper databaseHelper = new DatabaseHelper(CreateAdvert.this);
        Log.d("FINAL LOCATION::::::::", finalLocation);
        boolean isInserted = databaseHelper.insertData(name, phone, desc, date, location, type);
        if (!isInserted) {
            createToast(CreateAdvert.this, "Data is NOT inserted to database!").show();
            return;
        } else {
            createToast(CreateAdvert.this, "Data is inserted to database!").show();
            resetFormValues();
            return;
        }
    }

    private Toast createToast(Context context, String message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    private void resetFormValues() {
        editName.setText(null);
        editPhone.setText(null);
        editLocation.setText(null);
        editDescription.setText(null);
        editDate.setText(null);
    }
}
