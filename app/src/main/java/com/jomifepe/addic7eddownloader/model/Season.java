package com.jomifepe.addic7eddownloader.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;
import java.util.Objects;

@Entity(foreignKeys = { @ForeignKey(entity = TVShow.class,
                                    parentColumns = "addic7edId",
                                    childColumns = "showId") },
        indices = { @Index(value = "showId") })

public class Season extends Record implements Parcelable {
    /* Database specific attributes */
    private Integer showId;

    private Integer number;
    private Integer numberOfEpisodes;

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

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Season %d", number);
    }

    public Integer getShowId() {
        return showId;
    }

    public Integer getNumber() {
        return number;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    @Ignore
    protected Season(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            showId = null;
        } else {
            showId = in.readInt();
        }
        if (in.readByte() == 0) {
            number = null;
        } else {
            number = in.readInt();
        }
        if (in.readByte() == 0) {
            numberOfEpisodes = null;
        } else {
            numberOfEpisodes = in.readInt();
        }
    }

    public static final Creator<Season> CREATOR = new Creator<Season>() {
        @Override
        public Season createFromParcel(Parcel in) {
            return new Season(in);
        }

        @Override
        public Season[] newArray(int size) {
            return new Season[size];
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
        if (showId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(showId);
        }
        if (number == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(number);
        }
        if (numberOfEpisodes == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(numberOfEpisodes);
        }
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
