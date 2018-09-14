package com.jomifepe.addic7eddownloader.model.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.jomifepe.addic7eddownloader.model.Subtitle;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SubtitleDao extends BaseDao<Subtitle> {
    @Query("SELECT * FROM Subtitle WHERE episodeId = :episodeId")
    LiveData<List<Subtitle>> getSubtitles(Integer episodeId);

    @Query("SELECT COUNT(*) FROM Subtitle WHERE episodeId = :episodeId")
    int getSubtitleCount(Integer episodeId);

    @Query("DELETE FROM Subtitle") void deleteAll();
}
