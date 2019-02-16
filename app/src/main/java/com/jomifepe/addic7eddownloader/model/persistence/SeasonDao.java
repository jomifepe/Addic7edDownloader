package com.jomifepe.addic7eddownloader.model.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jomifepe.addic7eddownloader.model.Season;

import java.util.List;

@Dao
public interface SeasonDao extends BaseDao<Season> {
    @Query("SELECT * FROM Seasons WHERE showId = :showId")
    LiveData<List<Season>> getSeasons(Integer showId);

    @Query("SELECT COUNT(*) FROM Seasons WHERE showId = :showId")
    int getSeasonCount(Integer showId);

    @Query("DELETE FROM Seasons") void deleteAll();
}
