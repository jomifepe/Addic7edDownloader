package com.jomifepe.addic7eddownloader.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import junit.framework.Assert;

import java.util.ArrayList;

@Entity(inheritSuperIndices = true)
public class TVShow extends Media implements Parcelable {
//    @NonNull private Integer numberOfSeasons;
//    @NonNull private Integer numberOfEpisodes;

    public TVShow(Integer addic7edId, String title, String imageURL) {
        super(addic7edId, title, MediaType.TV_SHOW, imageURL);
    }

    @Ignore
    public TVShow(Integer id, String title) {
        this(id, title, null);
    }

    @Override
    public String toString() {
        return title;
    }

    @Ignore
    protected TVShow(Parcel in) {
        super(in);
    }

    public static final Creator<TVShow> CREATOR = new Creator<TVShow>() {
        @Override
        public TVShow createFromParcel(Parcel in) {
            return new TVShow(in);
        }

        @Override
        public TVShow[] newArray(int size) {
            return new TVShow[size];
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
