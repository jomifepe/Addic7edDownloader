package com.jomifepe.addic7eddownloader.model.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.jomifepe.addic7eddownloader.model.TVShow;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TVShowDao extends BaseDao<TVShow> {
    @Query("SELECT * FROM TVShow")
    LiveData<List<TVShow>> getShows();

    @Query("SELECT * FROM TVShow WHERE id = :showId")
    TVShow getShowById(Integer showId);

    @Query("SELECT COUNT(*) FROM TVShow")
    int getShowCount();

    @Query("DELETE FROM TVShow") void deleteAll();
}
