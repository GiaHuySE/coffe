package com.example.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Table implements Parcelable {
    private int id;
    private String name;
    private String status;
    private int seat;
    private Area area;

    public Table(int id, String name, String status, int seat, Area area) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.seat = seat;
        this.area = area;
    }

    public Table() {
    }

    protected Table(Parcel in) {
        id = in.readInt();
        name = in.readString();
        status = in.readString();
        seat = in.readInt();
        area = in.readParcelable(Area.class.getClassLoader());
    }

    public static final Creator<Table> CREATOR = new Creator<Table>() {
        @Override
        public Table createFromParcel(Parcel in) {
            return new Table(in);
        }

        @Override
        public Table[] newArray(int size) {
            return new Table[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(status);
        dest.writeInt(seat);
        dest.writeParcelable(area, flags);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
