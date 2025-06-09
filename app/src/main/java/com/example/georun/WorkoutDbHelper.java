package com.example.georun;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "workout.db";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_WORKOUTS = "workouts";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_CADENCE = "cadence";
    public static final String COLUMN_RESULT = "result"; // pace or speed
    private static final String COLUMN_DATE = "date";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LNG = "longitude";

    public WorkoutDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_WORKOUTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_DURATION + " INTEGER, " +
                COLUMN_DISTANCE + " REAL, " +
                COLUMN_CADENCE + " INTEGER, " +
                COLUMN_RESULT + " REAL, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_LAT + " REAL, " +
                COLUMN_LNG + " REAL)";
        db.execSQL(CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        onCreate(db);
    }

    public void insertWorkout(workoutModel workout) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TYPE, workout.getType());
        values.put(COLUMN_DURATION, workout.getDuration());
        values.put(COLUMN_DISTANCE, workout.getDistance());
        values.put(COLUMN_CADENCE, workout.getCadence());
        values.put(COLUMN_RESULT, workout.getResult());
        values.put(COLUMN_DATE, workout.getDate());
        values.put(COLUMN_LAT, workout.getLat());
        values.put(COLUMN_LNG, workout.getLng());

        db.insert(TABLE_WORKOUTS, null, values);
    }

    public List<workoutModel> getAllWorkouts() {
        List<workoutModel> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WORKOUTS, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                float distance = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_DISTANCE));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                int cadence = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CADENCE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LNG));

                float paceOrSpeed = 0;
                if (distance > 0 && duration > 0) {
                    if (type.equalsIgnoreCase("Running")) {
                        paceOrSpeed = duration / distance;
                    } else {
                        paceOrSpeed = (float) (distance / (duration / 60.0));
                    }
                }

                workouts.add(new workoutModel(id, date, lat, lng, type, duration, distance, cadence, paceOrSpeed));
            } while (cursor.moveToNext());
        }

        cursor.close();
        // Do NOT close db here
        return workouts;
    }


    public List<workoutModel> getAllWorkoutLocations() {
        List<workoutModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_WORKOUTS +
                        " WHERE " + COLUMN_LAT + " IS NOT NULL AND " + COLUMN_LNG + " IS NOT NULL",
                null);

        if (c.moveToFirst()) {
            int iType     = c.getColumnIndexOrThrow(COLUMN_TYPE);
            int iDuration = c.getColumnIndexOrThrow(COLUMN_DURATION);
            int iDistance = c.getColumnIndexOrThrow(COLUMN_DISTANCE);
            int iCadence  = c.getColumnIndexOrThrow(COLUMN_CADENCE);
            int iDate     = c.getColumnIndexOrThrow(COLUMN_DATE);
            int iLat      = c.getColumnIndexOrThrow(COLUMN_LAT);
            int iLng      = c.getColumnIndexOrThrow(COLUMN_LNG);

            do {
                String type   = c.getString(iType);
                int    dur    = c.getInt(iDuration);
                float  dist   = c.getFloat(iDistance);
                int    cad    = c.getInt(iCadence);
                String date   = c.getString(iDate);
                double lat    = c.getDouble(iLat);
                double lng    = c.getDouble(iLng);

                // use the constructor that matches your data
                workoutModel w = new workoutModel(type, dur, dist, cad);
                w.setDate(date);
                w.setLat(lat);
                w.setLng(lng);

                list.add(w);

            } while (c.moveToNext());
        }

        c.close();
        return list;      // don’t close the db here; let the helper manage it
    }

//public void deleteAllWorkouts() {
//    SQLiteDatabase db = this.getWritableDatabase();
//    db.delete(TABLE_WORKOUTS, null, null);
//    db.close();
//}

}

