package com.jomifepe.addic7eddownloader.model.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.jomifepe.addic7eddownloader.model.Record;

import java.util.List;

public interface BaseDao<R extends Record> {
    @Insert void insert(R... rs);
    @Insert void insert(List<R> rs);
    @Update void update(R... rs);
    @Delete void delete(R r);
}
