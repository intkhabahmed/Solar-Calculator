package com.intkhabahmed.solarcalculator.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "pins")
public class PinInfo implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    public static final Creator<PinInfo> CREATOR = new Creator<PinInfo>() {
        @Override
        public PinInfo createFromParcel(Parcel in) {
            return new PinInfo(in);
        }

        @Override
        public PinInfo[] newArray(int size) {
            return new PinInfo[size];
        }
    };
    private double latitude;
    private String placeName;
    private double longitude;

    @Ignore
    public PinInfo() {
    }

    public PinInfo(int id, double latitude, double longitude, String placeName) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
    }

    @Ignore
    public PinInfo(double latitude, double longitude, String placeName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
    }

    @Ignore
    private PinInfo(Parcel in) {
        id = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        placeName = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(placeName);
    }
}
