package com.jomifepe.addic7eddownloader.model.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.jomifepe.addic7eddownloader.model.Episode;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface EpisodeDao extends BaseDao<Episode> {
    @Query("SELECT * FROM Episodes WHERE seasonId = :seasonId")
    LiveData<List<Episode>> getEpisodes(Integer seasonId);

    @Query("SELECT COUNT(*) FROM Episodes WHERE seasonId = :seasonId")
    int getEpisodeCount(Integer seasonId);

    @Query("DELETE FROM Episodes") void deleteAll();
}
