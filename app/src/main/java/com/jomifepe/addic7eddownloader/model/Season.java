package com.jomifepe.addic7eddownloader.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import android.os.Parcelable;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

import java.util.Locale;
import java.util.Objects;

@Parcel(Serialization.BEAN)
@Entity(tableName = "Seasons",
        foreignKeys = {
            @ForeignKey(entity = Show.class,
                        parentColumns = "addic7edId",
                        childColumns = "showId")
        },
        indices = {
            @Index(value = "showId")
        })
public class Season extends Record {
    /* Database specific attributes */
    private Integer showId;

    private Integer number;
    private Integer numberOfEpisodes;

    @ParcelConstructor
    public Season(Integer id, Integer showId, Integer number, Integer numberOfEpisodes) {
        super(id);
        this.showId = showId;
        this.number = number;
        this.numberOfEpisodes = numberOfEpisodes;
    }

    @Ignore
    public Season(Integer showId, Integer number, Integer numberOfEpisodes) {
        this.showId = showId;
        this.number = number;
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public Integer getShowId() {
        return showId;
    }

    public Season setShowId(Integer showId) {
        this.showId = showId;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public Season setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public Season setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
        return this;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Season %d", number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Season season = (Season) o;
        return Objects.equals(showId, season.showId) &&
                Objects.equals(number, season.number) &&
                Objects.equals(numberOfEpisodes, season.numberOfEpisodes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(showId, number, numberOfEpisodes);
    }
}
