package com.intkhabahmed.solarcalculator.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "pins")
public class PinInfo implements Parcelable {
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
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "place_name")
    private String placeName;
    @ColumnInfo(name = "place_id")
    private String placeId;

    @Ignore
    public PinInfo(String placeName, String placeId) {
        this.placeName = placeName;
        this.placeId = placeId;
    }

    public PinInfo(int id, String placeName, String placeId) {
        this.id = id;
        this.placeName = placeName;
        this.placeId = placeId;
    }

    @Ignore
    private PinInfo(Parcel in) {
        id = in.readInt();
        placeName = in.readString();
        placeId = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(placeName);
        dest.writeString(placeId);
    }
}
