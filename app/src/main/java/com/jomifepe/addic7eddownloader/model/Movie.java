package com.jomifepe.addic7eddownloader.model;

import android.os.Parcelable;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

import java.util.Objects;

@Parcel(Serialization.BEAN)
public class Movie extends Media implements Content {
    private String pageUrl;

    @ParcelConstructor
    public Movie(Integer id, Integer addic7edId, String title, MediaType type, String posterURL,
                 String pageUrl) {
        super(id, addic7edId, title, type, posterURL);
        this.pageUrl = pageUrl;
    }

    public Movie(Integer addic7edId, String title, MediaType type, String posterURL,
                 String pageUrl) {
        super(addic7edId, title, type, posterURL);
        this.pageUrl = pageUrl;
    }

    public Movie(Integer addic7edId, String title, MediaType type, String pageUrl) {
        super(addic7edId, title, type);
        this.pageUrl = pageUrl;
    }

    @Override
    public String getPageUrl() {
        return pageUrl;
    }

    public Movie setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }
}
