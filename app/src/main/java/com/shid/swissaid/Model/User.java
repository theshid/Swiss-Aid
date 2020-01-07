package com.shid.swissaid.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String email;
    private String user_id;
    private String name;
    private String username;
    private String status;
    private String imageUrl;
    private String search;
    private String imagePath;

    public User() {

    }

    public User(String email, String user_id) {
        this.email = email;
        this.user_id = user_id;

    }

    public User(String email, String user_id, String name, String username, String status, String imageUrl,
                 String search, String imagePath) {
        this.email = email;
        this.user_id = user_id;
        this.name = name;
        this.username = username;
        this.status = status;
        this.imageUrl = imageUrl;
        this.search = search;
        this.imagePath = imagePath;
    }

    protected User(Parcel in) {
        email = in.readString();
        user_id = in.readString();
        name = in.readString();
        username = in.readString();
        status = in.readString();
        imageUrl = in.readString();
        search = in.readString();
        imagePath = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(user_id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(status);
        dest.writeString(imageUrl);
        dest.writeString(search);
        dest.writeString(imagePath);



    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                +
                        '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
