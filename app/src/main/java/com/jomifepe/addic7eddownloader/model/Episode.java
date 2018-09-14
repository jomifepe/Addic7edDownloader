package com.jomifepe.addic7eddownloader.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.jomifepe.addic7eddownloader.util.Util;

import junit.framework.Assert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;


@Entity(foreignKeys = { @ForeignKey(entity = Season.class,
                                    parentColumns = "id",
                                    childColumns = "seasonId") },
        indices = { @Index(value = "seasonId") })

public class Episode implements Parcelable {
    /* Database specific attributes */
    @PrimaryKey(autoGenerate = true) private Integer id;
    private Integer seasonId;

    private String title;
    private Integer season;
    private Integer number;
    private String pageURL;

    public Episode(Integer id, Integer seasonId, String title, Integer season, Integer number, String pageURL) {
        this.id = id;
        this.seasonId = seasonId;
        this.title = title;
        this.season = season;
        this.number = number;
        this.pageURL = pageURL;
    }

    @Ignore
    public Episode(Integer seasonId, String title, Integer season, Integer number, String pageURL) {
        this.seasonId = seasonId;
        this.title = title;
        this.season = season;
        this.number = number;
        this.pageURL = pageURL;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSeasonId() {
        return seasonId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getSeason() {
        return season;
    }

    public Integer getNumber() {
        return number;
    }

    public String getPageURL() {
        return pageURL;
    }

    @Ignore
    protected Episode(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            seasonId = null;
        } else {
            seasonId = in.readInt();
        }
        title = in.readString();
        if (in.readByte() == 0) {
            season = null;
        } else {
            season = in.readInt();
        }
        if (in.readByte() == 0) {
            number = null;
        } else {
            number = in.readInt();
        }
        pageURL = in.readString();
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
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
        if (seasonId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(seasonId);
        }
        parcel.writeString(title);
        if (season == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(season);
        }
        if (number == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(number);
        }
        parcel.writeString(pageURL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Episode episode = (Episode) o;
        return Objects.equals(seasonId, episode.seasonId) &&
                Objects.equals(title, episode.title) &&
                Objects.equals(season, episode.season) &&
                Objects.equals(number, episode.number) &&
                Objects.equals(pageURL, episode.pageURL);
    }

    @Override
    public int hashCode() {

        return Objects.hash(seasonId, title, season, number, pageURL);
    }
}

