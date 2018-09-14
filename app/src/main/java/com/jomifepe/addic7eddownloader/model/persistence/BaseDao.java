package com.jomifepe.addic7eddownloader.model.persistence;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

public interface BaseDao<E> {
    @Insert void insert(E... es);
    @Insert void insert(List<E> es);
    @Update void update(E... es);
    @Delete void delete(E e);
}
