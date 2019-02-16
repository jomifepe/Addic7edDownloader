package com.jomifepe.addic7eddownloader.model;

import android.os.Parcelable;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

@Parcel(Serialization.BEAN)
public class SearchResult implements Content {
    private String description;
    private MediaType type;
    private String url;

    @ParcelConstructor
    public SearchResult(String description, MediaType type, String url) {
        this.description = description;
        this.type = type;
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public SearchResult setDescription(String description) {
        this.description = description;
        return this;
    }

    public MediaType getType() {
        return type;
    }

    public SearchResult setType(MediaType type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public SearchResult setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String getPageUrl() {
        return url;
    }
}
