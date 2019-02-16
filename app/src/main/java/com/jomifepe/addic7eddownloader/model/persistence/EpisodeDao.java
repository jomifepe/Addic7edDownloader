package com.jomifepe.addic7eddownloader.model.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jomifepe.addic7eddownloader.model.Episode;

import java.util.List;

@Dao
public interface EpisodeDao extends BaseDao<Episode> {
    @Query("SELECT * FROM Episodes WHERE seasonId = :seasonId")
    LiveData<List<Episode>> getEpisodes(Integer seasonId);

    @Query("SELECT COUNT(*) FROM Episodes WHERE seasonId = :seasonId")
    int getEpisodeCount(Integer seasonId);

    @Query("DELETE FROM Episodes") void deleteAll();
}
