package com.jomifepe.addic7eddownloader.model.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.jomifepe.addic7eddownloader.model.Season;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SeasonDao extends BaseDao<Season> {
    @Query("SELECT * FROM Seasons WHERE showId = :showId")
    LiveData<List<Season>> getSeasons(Integer showId);

    @Query("SELECT COUNT(*) FROM Seasons WHERE showId = :showId")
    int getSeasonCount(Integer showId);

    @Query("DELETE FROM Seasons") void deleteAll();
}
