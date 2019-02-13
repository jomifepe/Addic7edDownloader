package com.jomifepe.addic7eddownloader.model.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.jomifepe.addic7eddownloader.model.Favorite;
import com.jomifepe.addic7eddownloader.model.Show;

import java.util.List;

@Dao
public interface FavoriteDao extends BaseDao<Favorite> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Favorite... favorites);

    @Query("SELECT * FROM Favorites")
    LiveData<List<Favorite>> getAll();

    @Query("SELECT T.* FROM Shows T, Favorites F " +
            "WHERE T.addic7edId = F.addic7edShowId " +
            "ORDER BY F.position")
    LiveData<List<Show>> getShows();

    @Query("SELECT * FROM Favorites ORDER BY id DESC LIMIT 1")
    Favorite getLast();

    @Query(("SELECT * FROM FAVORITES WHERE id = :id"))
    Favorite getById(Integer id);

    @Query(("SELECT * FROM FAVORITES WHERE addic7edShowId = :addic7edShowId"))
    Favorite getByAddic7edId(Integer addic7edShowId);

    @Query("SELECT COUNT(*) FROM Favorites")
    int getFavoriteCount();

    @Query("DELETE FROM Favorites") void deleteAll();

    @Query("DELETE FROM Favorites WHERE addic7edShowId = :addic7edShowId")
    void deleteById(Integer addic7edShowId);
}
