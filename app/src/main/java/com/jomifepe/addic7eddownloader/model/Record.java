package com.jomifepe.addic7eddownloader.model;

import android.os.Parcelable;

import org.parceler.Parcel;

import androidx.room.PrimaryKey;

@Parcel
public class Record {
    @PrimaryKey(autoGenerate = true) public Integer id;

    public Record() {}

    public Record(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
