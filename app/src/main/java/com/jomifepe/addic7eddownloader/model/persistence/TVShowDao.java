package com.jomifepe.addic7eddownloader.model.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.jomifepe.addic7eddownloader.model.Show;

import java.util.List;

@Dao
public interface TVShowDao extends BaseDao<Show> {
    @Query("SELECT * FROM Shows")
    LiveData<List<Show>> getAllShows();

    @Query("SELECT * FROM Shows WHERE id = :showId")
    Show getShowById(Integer showId);

    @Query("SELECT COUNT(*) FROM Shows")
    int getShowCount();

    @Query("DELETE FROM Shows")
    void deleteAll();
}
