package com.jomifepe.addic7eddownloader.model.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.jomifepe.addic7eddownloader.model.persistence.BaseDao;

import java.util.List;

public class BaseViewModel<E, DAO extends BaseDao<E>> extends AndroidViewModel {
    protected DAO dao;

    public BaseViewModel(@NonNull Application application, DAO dao) {
        super(application);
        this.dao = dao;
    }
    public void insert(List<E> es) {
        dao.insert(es);
    }

    public void insert(E... es) {
        dao.insert(es);
    }

    public void update(E... es) {
        dao.update(es);
    }

    public void delete(E e) {
        dao.delete(e);
    }
}
