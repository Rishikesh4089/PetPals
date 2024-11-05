package com.example.project_mad;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;

public class Location implements Parcelable {
    private String name;
    private String address;
    private String photo;
    private double latitude;
    private double longitude;
    private String type;

    public Location() {
    }

    public Location(String name, String address, String photo, double latitude, double longitude, String type) {
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoto() {
        return photo;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getType() {return type;}

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    protected Location(Parcel in) {
        name = in.readString();
        address = in.readString();
        photo = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(photo);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
