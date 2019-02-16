package com.jomifepe.addic7eddownloader.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(tableName = "Favorites",
        foreignKeys = {
            @ForeignKey(entity = Show.class,
                        parentColumns = "addic7edId",
                        childColumns = "addic7edShowId")
        },
        indices = {
            @Index(value = "addic7edShowId", unique = true)
        })

public class Favorite extends Record {
    private Integer addic7edShowId;
    private Integer position;

    public Favorite(Integer id, Integer addic7edShowId, Integer position) {
        super(id);
        this.addic7edShowId = addic7edShowId;
        this.position = position;
    }

    @Ignore
    public Favorite(Integer addic7edShowId, Integer position) {
        this.addic7edShowId = addic7edShowId;
        this.position = position;
    }

    public Integer getAddic7edShowId() {
        return addic7edShowId;
    }

    public Favorite setAddic7edShowId(Integer addic7edShowId) {
        this.addic7edShowId = addic7edShowId;
        return this;
    }

    public Integer getPosition() {
        return position;
    }

    public Favorite setPosition(Integer position) {
        this.position = position;
        return this;
    }
}
