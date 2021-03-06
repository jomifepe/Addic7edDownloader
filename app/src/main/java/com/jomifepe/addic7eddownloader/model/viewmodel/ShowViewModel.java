package com.jomifepe.addic7eddownloader.model.viewmodel;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.model.persistence.AppDatabase;
import com.jomifepe.addic7eddownloader.model.persistence.TVShowDao;

import org.parceler.Parcel;

import java.util.List;

public class ShowViewModel extends BaseViewModel<Show, TVShowDao> {
    private LiveData<List<Show>> tvShowsLiveData;

    public ShowViewModel(@NonNull Application application) {
        super(application, AppDatabase.getFileDatabase(application).tvShowDao());
        tvShowsLiveData = dao.getAllShows();
    }

    public LiveData<List<Show>> getTvShowsList() {
        return tvShowsLiveData;
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}
