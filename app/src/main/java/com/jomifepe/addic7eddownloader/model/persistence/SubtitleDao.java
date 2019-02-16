package com.jomifepe.addic7eddownloader.model.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jomifepe.addic7eddownloader.model.Subtitle;

import java.util.List;

@Dao
public interface SubtitleDao extends BaseDao<Subtitle> {
    @Query("SELECT * FROM Subtitles WHERE contentId = :episodeId")
    LiveData<List<Subtitle>> getSubtitles(Integer episodeId);

    @Query("SELECT COUNT(*) FROM Subtitles WHERE contentId = :episodeId")
    int getSubtitleCount(Integer episodeId);

    @Query("DELETE FROM Subtitles") void deleteAll();
}
