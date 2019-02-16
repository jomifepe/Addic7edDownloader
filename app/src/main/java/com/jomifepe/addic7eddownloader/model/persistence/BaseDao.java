package com.jomifepe.addic7eddownloader.model.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jomifepe.addic7eddownloader.model.Record;

import java.util.List;

public interface BaseDao<R extends Record> {
    @Insert void insert(R... rs);
    @Insert void insert(List<R> rs);
    @Update void update(R... rs);
    @Delete void delete(R r);
}
