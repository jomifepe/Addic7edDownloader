package com.jomifepe.addic7eddownloader.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.jomifepe.addic7eddownloader.model.persistence.typeconverter.MediaTypeConverter;

import java.util.Objects;

@Entity(indices = {@Index(value = "addic7edId", unique = true)})
public class Media extends Record implements Parcelable {
    public Integer addic7edId;
    protected String title;

    @Ignore
    @TypeConverters(MediaTypeConverter.class)
    public MediaType type;

    protected String posterURL;

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

    public String getTitle() {
        return title;
    }

    public MediaType getType() {
        return type;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public Media setPosterURL(String posterURL) {
        this.posterURL = posterURL;
        return this;
    }

    protected Media(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            addic7edId = null;
        } else {
            addic7edId = in.readInt();
        }
        title = in.readString();
        posterURL = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        if (addic7edId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(addic7edId);
        }
        parcel.writeString(title);
        parcel.writeString(posterURL);
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
