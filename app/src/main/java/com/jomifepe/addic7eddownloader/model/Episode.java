package com.jomifepe.addic7eddownloader.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import android.os.Parcelable;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

import java.util.Objects;

@Parcel(Serialization.BEAN)
@Entity(tableName = "Episodes",
        foreignKeys = {
                @ForeignKey(entity = Season.class,
                        parentColumns = "id",
                        childColumns = "seasonId")
        },
        indices = {
                @Index(value = "seasonId")
        })
public class Episode extends Record implements Content {
    /* Database specific attributes */
    private String pageURL;
    private Integer seasonId;

    private String title;
    private Integer seasonNumber;
    private Integer number;

    @ParcelConstructor
    public Episode(Integer id, Integer seasonId, String title, Integer seasonNumber, Integer number,
                   String pageURL) {
        super(id);
        this.seasonId = seasonId;
        this.title = title;
        this.seasonNumber = seasonNumber;
        this.number = number;
        this.pageURL = pageURL;
    }

    @Ignore
    public Episode(Integer seasonId, String title, Integer seasonNumber, Integer number,
                   String pageURL) {
        this.seasonId = seasonId;
        this.title = title;
        this.seasonNumber = seasonNumber;
        this.number = number;
        this.pageURL = pageURL;
    }

    public String getPageURL() {
        return pageURL;
    }

    public Episode setPageURL(String pageURL) {
        this.pageURL = pageURL;
        return this;
    }

    public Integer getSeasonId() {
        return seasonId;
    }

    public Episode setSeasonId(Integer seasonId) {
        this.seasonId = seasonId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Episode setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public Episode setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public Episode setNumber(Integer number) {
        this.number = number;
        return this;
    }

    @Override
    public String getPageUrl() {
        return pageURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Episode episode = (Episode) o;
        return Objects.equals(seasonId, episode.seasonId) &&
                Objects.equals(title, episode.title) &&
                Objects.equals(seasonNumber, episode.seasonNumber) &&
                Objects.equals(number, episode.number) &&
                Objects.equals(pageURL, episode.pageURL);
    }

    @Override
    public int hashCode() {

        return Objects.hash(seasonId, title, seasonNumber, number, pageURL);
    }
}


