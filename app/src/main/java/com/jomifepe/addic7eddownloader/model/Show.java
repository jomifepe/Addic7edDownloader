package com.jomifepe.addic7eddownloader.model;

import androidx.room.Entity;
import androidx.room.Ignore;

import android.os.Parcelable;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
@Entity(tableName = "Shows",
        inheritSuperIndices = true)
public class Show extends Media {
//    @NonNull private Integer numberOfSeasons;
//    @NonNull private Integer numberOfEpisodes;

    @ParcelConstructor
    public Show(Integer addic7edId, String title, String posterURL) {
        super(addic7edId, title, MediaType.SHOW, posterURL);
    }

    @Ignore
    public Show(Integer addic7edId, String title) {
        this(addic7edId, title, null);
    }

    @Override
    public String toString() {
        return title;
    }
}
