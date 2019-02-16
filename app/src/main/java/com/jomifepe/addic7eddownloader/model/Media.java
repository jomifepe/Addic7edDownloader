package com.jomifepe.addic7eddownloader.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.TypeConverters;
import android.os.Parcelable;

import com.jomifepe.addic7eddownloader.model.persistence.typeconverter.MediaTypeConverter;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

import java.util.Objects;

@Parcel(Serialization.BEAN)
@Entity(indices = {@Index(value = "addic7edId", unique = true)})
public class Media extends Record {
    public Integer addic7edId;
    protected String title;

    @Ignore
    @TypeConverters(MediaTypeConverter.class)
    public MediaType type;

    protected String posterURL;

    @ParcelConstructor
    public Media(Integer id, Integer addic7edId, String title, MediaType type, String posterURL) {
        super(id);
        this.addic7edId = addic7edId;
        this.title = title;
        this.type = type;
        this.posterURL = posterURL;
    }

    @Ignore
    public Media(Integer addic7edId, String title, MediaType type, String posterURL) {
        this.addic7edId = addic7edId;
        this.title = title;
        this.type = type;
        this.posterURL = posterURL;
    }

    @Ignore
    public Media(Integer addic7edId, String title, MediaType type) {
        this(addic7edId, title, type, null);
    }

    public Integer getAddic7edId() {
        return addic7edId;
    }

    public Media setAddic7edId(Integer addic7edId) {
        this.addic7edId = addic7edId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Media setTitle(String title) {
        this.title = title;
        return this;
    }

    public MediaType getType() {
        return type;
    }

    public Media setType(MediaType type) {
        this.type = type;
        return this;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public Media setPosterURL(String posterURL) {
        this.posterURL = posterURL;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return Objects.equals(addic7edId, media.addic7edId) &&
                Objects.equals(title, media.title) &&
                type == media.type &&
                Objects.equals(posterURL, media.posterURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addic7edId, title, type, posterURL);
    }
}
