package com.jomifepe.addic7eddownloader.model.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.jomifepe.addic7eddownloader.model.TVShow;
import com.jomifepe.addic7eddownloader.model.persistence.AppDatabase;
import com.jomifepe.addic7eddownloader.model.persistence.TVShowDao;

import java.util.List;

public class TVShowViewModel extends BaseViewModel<TVShow, TVShowDao> {
    private LiveData<List<TVShow>> tvShowsLiveData;

    public TVShowViewModel(@NonNull Application application) {
        super(application, AppDatabase.getFileDatabase(application).tvShowDao());
        tvShowsLiveData = dao.getShows();
    }

    public LiveData<List<TVShow>> getTvShowsList() {
        return tvShowsLiveData;
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}
