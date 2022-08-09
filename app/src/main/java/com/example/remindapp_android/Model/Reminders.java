package com.example.remindapp_android.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class Reminders implements Parcelable {
    String title;
    String notes;
    String date;
    String id;
    GeoPoint location;

    public Reminders() {
    }

    public Reminders(String title, String notes, String date, String id, GeoPoint location) {
        this.title = title;
        this.notes = notes;
        this.date = date;
        this.id = id;
        this.location = location;
    }

    protected Reminders(Parcel in) {
       Double lat = in.readDouble();
       Double lng = in.readDouble();
       location = new GeoPoint(lat,lng);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
       parcel.writeDouble(location.getLatitude());
       parcel.writeDouble(location.getLongitude());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Reminders> CREATOR = new Creator<Reminders>() {
        @Override
        public Reminders createFromParcel(Parcel in) {
            return new Reminders(in);
        }

        @Override
        public Reminders[] newArray(int size) {
            return new Reminders[size];
        }
    };

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
