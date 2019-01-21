package com.jomifepe.addic7eddownloader.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;
import java.util.Objects;

@Entity(foreignKeys = { @ForeignKey(entity = Episode.class,
                                    parentColumns = "id",
                                    childColumns = "episodeId") },
        indices = { @Index(value = "episodeId") })

public class Subtitle extends Record implements Parcelable {
    /* Database specific attributes */
    private Integer episodeId;

    private String language;
    private String version;
    private boolean corrected;
    private boolean hearingImpaired;
    private boolean hd;
    private String downloadURL;

    public Subtitle(Integer id, Integer episodeId, String language, String version,
                    boolean corrected, boolean hearingImpaired, boolean hd, String downloadURL) {
        super(id);
        this.episodeId = episodeId;
        this.language = language;
        this.version = version;
        this.corrected = corrected;
        this.hearingImpaired = hearingImpaired;
        this.hd = hd;
        this.downloadURL = downloadURL;
    }

    @Ignore
    public Subtitle(Integer episodeId, String language, String version,
                    boolean corrected, boolean hearingImpaired, boolean hd, String downloadURL) {
        this.episodeId = episodeId;
        this.language = language;
        this.version = version;
        this.corrected = corrected;
        this.hearingImpaired = hearingImpaired;
        this.hd = hd;
        this.downloadURL = downloadURL;
    }

    @Ignore
    public Subtitle(Integer episodeId, String language, String version,
                    boolean corrected, boolean hearingImpaired, boolean hd) {
        this(episodeId, language, version, corrected, hearingImpaired, hd, null);
    }

    public Integer getEpisodeId() {
        return episodeId;
    }

    public String getLanguage() {
        return language;
    }

    public String getVersion() {
        return version;
    }

    public boolean isCorrected() {
        return corrected;
    }

    public boolean isHearingImpaired() {
        return hearingImpaired;
    }

    public boolean isHd() {
        return hd;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s - %s%s%s%s",
                getLanguage(), getVersion(), isHearingImpaired() ? " HI" : "",
                isCorrected() ? " Corrected" : "", isHd() ? " 720/1080" : "");
    }

    @Ignore
    protected Subtitle(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            episodeId = null;
        } else {
            episodeId = in.readInt();
        }
        language = in.readString();
        version = in.readString();
        corrected = in.readByte() != 0;
        hearingImpaired = in.readByte() != 0;
        hd = in.readByte() != 0;
        downloadURL = in.readString();
    }

    public static final Creator<Subtitle> CREATOR = new Creator<Subtitle>() {
        @Override
        public Subtitle createFromParcel(Parcel in) {
            return new Subtitle(in);
        }

        @Override
        public Subtitle[] newArray(int size) {
            return new Subtitle[size];
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
        if (episodeId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(episodeId);
        }
        parcel.writeString(language);
        parcel.writeString(version);
        parcel.writeByte((byte) (corrected ? 1 : 0));
        parcel.writeByte((byte) (hearingImpaired ? 1 : 0));
        parcel.writeByte((byte) (hd ? 1 : 0));
        parcel.writeString(downloadURL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtitle subtitle = (Subtitle) o;
        return corrected == subtitle.corrected &&
                hearingImpaired == subtitle.hearingImpaired &&
                hd == subtitle.hd &&
                Objects.equals(episodeId, subtitle.episodeId) &&
                Objects.equals(language, subtitle.language) &&
                Objects.equals(version, subtitle.version) &&
                Objects.equals(downloadURL, subtitle.downloadURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(episodeId, language, version, corrected, hearingImpaired, hd, downloadURL);
    }
}
