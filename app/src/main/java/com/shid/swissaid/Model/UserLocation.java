package com.shid.swissaid.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation implements Parcelable {

    private GeoPoint geo_point;
    private @ServerTimestamp
    Date timestamp;
    private User user;

    public UserLocation(User user, GeoPoint geo_point,Date timestamp) {
        this.geo_point = geo_point;
        this.timestamp = timestamp;
        this.user = user;
    }

    public UserLocation() {

    }

    protected UserLocation(Parcel in) {
        double lat = in.readDouble();
        double lon = in.readDouble();
        geo_point = new GeoPoint(lat, lon);
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "geo_point=" + geo_point +
                ", timestamp='" + timestamp + '\'' +
                ", user=" + user +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(geo_point.getLatitude());
        dest.writeDouble(geo_point.getLongitude());
        dest.writeParcelable(user, flags);
    }
}

