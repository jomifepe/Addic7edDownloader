package com.jomifepe.addic7eddownloader.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import android.os.Parcelable;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.Parcel.Serialization;

import java.util.Locale;
import java.util.Objects;

@Parcel(Serialization.BEAN)
@Entity(tableName = "Subtitles",
        foreignKeys = {
                @ForeignKey(entity = Episode.class,
                            parentColumns = "id",
                            childColumns = "contentId")
        },
        indices = {
            @Index(value = "contentId")
        })
public class Subtitle extends Record {
    /* the id of an episode or movie */
    private Integer contentId;

    private String language;
    private String version;
    private boolean corrected;
    private boolean hearingImpaired;
    private boolean hd;
    private String downloadURL;

    @ParcelConstructor
    public Subtitle(Integer id, Integer contentId, String language, String version,
                    boolean corrected, boolean hearingImpaired, boolean hd, String downloadURL) {
        super(id);
        this.contentId = contentId;
        this.language = language;
        this.version = version;
        this.corrected = corrected;
        this.hearingImpaired = hearingImpaired;
        this.hd = hd;
        this.downloadURL = downloadURL;
    }

    @Ignore
    public Subtitle(Integer contentId, String language, String version,
                    boolean corrected, boolean hearingImpaired, boolean hd, String downloadURL) {
        this.contentId = contentId;
        this.language = language;
        this.version = version;
        this.corrected = corrected;
        this.hearingImpaired = hearingImpaired;
        this.hd = hd;
        this.downloadURL = downloadURL;
    }

    @Ignore
    public Subtitle(Integer contentId, String language, String version,
                    boolean corrected, boolean hearingImpaired, boolean hd) {
        this(contentId, language, version, corrected, hearingImpaired, hd, null);
    }

    public Integer getContentId() {
        return contentId;
    }

    public Subtitle setContentId(Integer contentId) {
        this.contentId = contentId;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public Subtitle setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Subtitle setVersion(String version) {
        this.version = version;
        return this;
    }

    public boolean isCorrected() {
        return corrected;
    }

    public Subtitle setCorrected(boolean corrected) {
        this.corrected = corrected;
        return this;
    }

    public boolean isHearingImpaired() {
        return hearingImpaired;
    }

    public Subtitle setHearingImpaired(boolean hearingImpaired) {
        this.hearingImpaired = hearingImpaired;
        return this;
    }

    public boolean isHd() {
        return hd;
    }

    public Subtitle setHd(boolean hd) {
        this.hd = hd;
        return this;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public Subtitle setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
        return this;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s - %s%s%s%s",
                getLanguage(), getVersion(), isHearingImpaired() ? " HI" : "",
                isCorrected() ? " Corrected" : "", isHd() ? " 720/1080" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtitle subtitle = (Subtitle) o;
        return corrected == subtitle.corrected &&
                hearingImpaired == subtitle.hearingImpaired &&
                hd == subtitle.hd &&
                Objects.equals(contentId, subtitle.contentId) &&
                Objects.equals(language, subtitle.language) &&
                Objects.equals(version, subtitle.version) &&
                Objects.equals(downloadURL, subtitle.downloadURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentId, language, version, corrected, hearingImpaired, hd, downloadURL);
    }
}
