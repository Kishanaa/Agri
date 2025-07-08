package com.aurorasphere.agri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SensorDataDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SensorData.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "SensorReadings";

    public static final String COLUMN_DATETIME = "datetime"; // PRIMARY KEY
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_HUMIDITY = "humidity";
    public static final String COLUMN_SOIL_MOISTURE = "soil_moisture";

    public SensorDataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_DATETIME + " TEXT PRIMARY KEY, " +
                COLUMN_TEMPERATURE + " INTEGER, " +
                COLUMN_HUMIDITY + " INTEGER, " +
                COLUMN_SOIL_MOISTURE + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrade (optional)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert sensor data into table
    public void insertSensorData(String datetime, int temperature, int humidity, int soilMoisture) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATETIME, datetime);
        values.put(COLUMN_TEMPERATURE, temperature);
        values.put(COLUMN_HUMIDITY, humidity);
        values.put(COLUMN_SOIL_MOISTURE, soilMoisture);

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Model class for one row
    public static class SensorData {
        public String datetime;
        public int temperature;
        public int humidity;
        public int soilMoisture;

        public SensorData(String datetime, int temperature, int humidity, int soilMoisture) {
            this.datetime = datetime;
            this.temperature = temperature;
            this.humidity = humidity;
            this.soilMoisture = soilMoisture;
        }
    }

    // Fetch all sensor data, newest first
    public List<SensorData> getAllSensorData() {
        List<SensorData> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATETIME + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String datetime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATETIME));
                int temperature = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEMPERATURE));
                int humidity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HUMIDITY));
                int soilMoisture = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SOIL_MOISTURE));

                SensorData data = new SensorData(datetime, temperature, humidity, soilMoisture);
                dataList.add(data);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return dataList;
    }
}

