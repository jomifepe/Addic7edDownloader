package com.jomifepe.addic7eddownloader.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Favorite {
    @PrimaryKey(autoGenerate = true) public Integer id;
    private Integer showId;
}
