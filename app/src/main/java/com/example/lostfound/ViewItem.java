package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ViewItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String location = intent.getStringExtra("location");
        String postType = intent.getStringExtra("post_type");
        String phone = intent.getStringExtra("phone");
        String date = intent.getStringExtra("date");
        String id = intent.getStringExtra("id");

        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        TextView textViewLocation = findViewById(R.id.textViewLocation);
        TextView textViewPostType = findViewById(R.id.textViewPostType);
        TextView textViewPhone = findViewById(R.id.textViewPhone);
        TextView textViewDate = findViewById(R.id.textViewDate);

        textViewName.setText("Name: " + name);
        textViewDescription.setText("Description: " + description);
        textViewLocation.setText("Location: " + location);
        textViewPostType.setText("Post Type: " + postType);
        textViewPhone.setText("Phone: " + phone);
        textViewDate.setText("Date: " + date);

        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper  = new DatabaseHelper(ViewItem.this);
                boolean isDeleted = databaseHelper.deleteDataById(Integer.parseInt(id));
                if (isDeleted) {
                    Toast.makeText(ViewItem.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ViewItem.this, ShowAllItems.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewItem.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
