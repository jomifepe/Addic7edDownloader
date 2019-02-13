package com.jomifepe.addic7eddownloader.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "Shows",
        inheritSuperIndices = true)
public class Show extends Media implements Parcelable {
//    @NonNull private Integer numberOfSeasons;
//    @NonNull private Integer numberOfEpisodes;

    public Show(Integer addic7edId, String title, String posterURL) {
        super(addic7edId, title, MediaType.TV_SHOW, posterURL);
    }

    @Ignore
    public Show(Integer id, String title) {
        this(id, title, null);
    }

    @Override
    public String toString() {
        return title;
    }

    @Ignore
    protected Show(Parcel in) {
        super(in);
    }

    public static final Creator<Show> CREATOR = new Creator<Show>() {
        @Override
        public Show createFromParcel(Parcel in) {
            return new Show(in);
        }

        @Override
        public Show[] newArray(int size) {
            return new Show[size];
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
