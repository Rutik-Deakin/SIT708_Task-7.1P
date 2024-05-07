package com.example.lostfound;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShowAllItems extends AppCompatActivity {

    ListView listView;
    ArrayList<String> namesList;
    ArrayAdapter<String> adapter;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_items);

        listView = findViewById(R.id.listView);
        namesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, namesList);
        listView.setAdapter(adapter);

        databaseHelper = new DatabaseHelper(ShowAllItems.this);
        populateListView();

        // Setting click listener for the ListView items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedName = (String) parent.getItemAtPosition(position);
                // Query the database to get all data associated with the selected name
                Cursor cursor = databaseHelper.getDataByName(selectedName);
                // Handle the retrieved data, e.g., display in a dialog or navigate to another activity
                if (cursor != null && cursor.moveToFirst()) {
                    // Extract data from cursor and use it as needed
                    int dataId = cursor.getInt(cursor.getColumnIndex("id"));
                    Log.d("FINAL!!!!!!!!!!!", dataId+"");
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String phone = cursor.getString(cursor.getColumnIndex("phone"));
                    String description = cursor.getString(cursor.getColumnIndex("description"));
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    String location = cursor.getString(cursor.getColumnIndex("location"));
                    String postType = cursor.getString(cursor.getColumnIndex("post_type"));


                    // Do something with the data, e.g., display in a dialog or navigate to another activity
                    Intent intent = new Intent(ShowAllItems.this, ViewItem.class);
                    intent.putExtra("id", dataId+"");
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
        });
    }

    private void populateListView() {
        Cursor cursor = databaseHelper.getAllData();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                namesList.add(name);
            } while (cursor.moveToNext());
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }
}
