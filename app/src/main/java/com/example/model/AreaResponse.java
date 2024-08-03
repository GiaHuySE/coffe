package com.example.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AreaResponse implements Parcelable {
    private int id;
    private String name;
    private List<Table> tables;

    // Default constructor
    public AreaResponse() {}

    // Constructor for Parcel
    protected AreaResponse(Parcel in) {
        id = in.readInt();
        name = in.readString();
        tables = in.createTypedArrayList(Table.CREATOR);
    }

    public static final Creator<AreaResponse> CREATOR = new Creator<AreaResponse>() {
        @Override
        public AreaResponse createFromParcel(Parcel in) {
            return new AreaResponse(in);
        }

        @Override
        public AreaResponse[] newArray(int size) {
            return new AreaResponse[size];
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
        dest.writeTypedList(tables);
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

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "AreaResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tables=" + tables +
                '}';
    }
}
