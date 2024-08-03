package com.example.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Area implements Parcelable {
    private int id;
    private String name;

    // Constructor
    public Area(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Parcelable implementation
    protected Area(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public Area() {
    }

    public static final Creator<Area> CREATOR = new Creator<Area>() {
        @Override
        public Area createFromParcel(Parcel in) {
            return new Area(in);
        }

        @Override
        public Area[] newArray(int size) {
            return new Area[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
