package com.example.georun;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class workoutModel {
    private int id;
    private String date;
    private double lat;
    private double lng;
    private String type;
    private int duration; // in minutes
    private float distance; // in kilometers
    private int cadence;
    private float result; // pace (min/km) or speed (km/h)

    public workoutModel(int id, String date, double lat, double lng, String type, int duration, float distance, int cadence, float result) {
        this.id = id;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.duration = duration;
        this.distance = distance;
        this.cadence = cadence;
        this.result = result;
    }

    public workoutModel(String type, int duration, float distance, int cadence) {
        this.type = type;
        this.duration = duration;
        this.distance = distance;
        this.cadence = cadence;
        this.date = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
        calculateResult();
    }

    public workoutModel(String type, int duration, float distance, int cadence, double latitude, double longitude) {
    }

    private void calculateResult() {
        if ("Running".equalsIgnoreCase(type)) {
            this.result = distance != 0 ? (float) duration / distance : 0; // pace (min/km)
        } else if ("Cycling".equalsIgnoreCase(type)) {
            this.result = duration != 0 ? (distance / (duration / 60f)) : 0; // speed (km/h)
        } else {
            this.result = 0;
        }
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        calculateResult();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        calculateResult();
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
        calculateResult();
    }

    public int getCadence() {
        return cadence;
    }

    public void setCadence(int cadence) {
        this.cadence = cadence;
    }

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "WorkoutModel{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", type='" + type + '\'' +
                ", duration=" + duration +
                ", distance=" + distance +
                ", cadence=" + cadence +
                ", result=" + result +
                '}';
    }
}
