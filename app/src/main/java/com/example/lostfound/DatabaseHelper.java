package com.example.lostfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "advert_database";
    private static final String TABLE_NAME = "adverts";
    private static final String COL_NAME = "name";
    private static final String COL_PHONE = "phone";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_DATE = "date";
    private static final String COL_LOCATION = "location";
    private static final String COL_POST_TYPE = "post_type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_LOCATION + " TEXT, " +
                COL_POST_TYPE + " TEXT)";
        db.execSQL(createTableQuery);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, String phone, String description, String date, String location, String postType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_PHONE, phone);
        contentValues.put(COL_DESCRIPTION, description);
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_LOCATION, location);
        contentValues.put(COL_POST_TYPE, postType);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getDataByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"id", COL_NAME, COL_PHONE, COL_DESCRIPTION, COL_DATE, COL_LOCATION, COL_POST_TYPE};
        String selection = COL_NAME + " = ?";
        String[] selectionArgs = {name};
        return db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

    public boolean deleteDataById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Define the WHERE clause to delete the row with the given ID
        String whereClause = "id=?";
        // Specify the ID value in the selection arguments
        String[] whereArgs = {String.valueOf(id)};
        // Perform the deletion operation
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        // Return true if at least one row was deleted, false otherwise
        return rowsDeleted > 0;
    }


}
