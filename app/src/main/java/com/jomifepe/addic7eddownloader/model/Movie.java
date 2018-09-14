package com.jomifepe.addic7eddownloader.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie extends Media implements Parcelable {

    public Movie(String title, Integer id, String imageURL) {
        super(id, title, MediaType.MOVIE, imageURL);
    }

    public Movie(String title, Integer id) {
        super(id, title, MediaType.MOVIE, null);
    }

    protected Movie(Parcel in) {
        super(in);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
    }
}
