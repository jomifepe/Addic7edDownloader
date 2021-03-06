package com.jomifepe.addic7eddownloader.model.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.jomifepe.addic7eddownloader.model.Record;
import com.jomifepe.addic7eddownloader.model.persistence.BaseDao;

import org.parceler.Parcel;

import java.util.List;

public class BaseViewModel<E extends Record, DAO extends BaseDao<E>> extends AndroidViewModel {
    protected DAO dao;

    public BaseViewModel(@NonNull Application application, DAO dao) {
        super(application);
        this.dao = dao;
    }

    public void insert(List<E> es) {
        dao.insert(es);
    }

    @SafeVarargs
    public final void insert(E... es) {
        dao.insert(es);
    }

    @SafeVarargs
    public final void update(E... es) {
        dao.update(es);
    }

    public void delete(E e) {
        dao.delete(e);
    }
}
