package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CreateAdvert extends AppCompatActivity {

    private EditText editName, editPhone, editDescription, editDate, editLocation;
    private Button btnSave;
    private RadioGroup radioPostType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        radioPostType = findViewById(R.id.radioPostType);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editDescription = findViewById(R.id.editDescription);
        editDate = findViewById(R.id.editDate);
        editLocation = findViewById(R.id.editLocation);
        btnSave = findViewById(R.id.btnSave);

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
                createAdvert(selectedPostType, name, phone, desc, date, location);
            }
        });
    }

    private void createAdvert(String type, String name, String phone, String desc, String date, String location) {
        DatabaseHelper databaseHelper = new DatabaseHelper(CreateAdvert.this);
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
    private Toast createToast(Context context, String message){
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }
    private void resetFormValues(){
        editName.setText(null);
        editPhone.setText(null);
        editLocation.setText(null);
        editDescription.setText(null);
        editDate.setText(null);
    }
}