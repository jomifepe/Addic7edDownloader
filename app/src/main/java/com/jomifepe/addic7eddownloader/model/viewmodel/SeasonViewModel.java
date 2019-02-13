package com.jomifepe.addic7eddownloader.model.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.model.persistence.AppDatabase;
import com.jomifepe.addic7eddownloader.model.persistence.SeasonDao;

import java.util.List;

public class SeasonViewModel extends BaseViewModel<Season, SeasonDao> {
    public static class SeasonViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application application;
        private Show show;

        public SeasonViewModelFactory(Application application, Show show) {
            this.show = show;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new SeasonViewModel(application, show);
        }
    }

    private LiveData<List<Season>> seasonsLiveData;

    public SeasonViewModel(@NonNull Application application, Show show) {
        super(application, AppDatabase.getFileDatabase(application).seasonDao());
        seasonsLiveData = dao.getSeasons(show.getAddic7edId());
    }

    public LiveData<List<Season>> getSeasonsList() {
        return seasonsLiveData;
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}
