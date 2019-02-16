package com.jomifepe.addic7eddownloader.model.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

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
