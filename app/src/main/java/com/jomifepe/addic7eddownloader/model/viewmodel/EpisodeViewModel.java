package com.jomifepe.addic7eddownloader.model.viewmodel;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.model.persistence.AppDatabase;
import com.jomifepe.addic7eddownloader.model.persistence.EpisodeDao;

import java.util.List;

public class EpisodeViewModel extends BaseViewModel<Episode, EpisodeDao> {
    public static class EpisodeViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application application;
        private Season season;

        public EpisodeViewModelFactory(Application application, Season season) {
            this.season = season;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new EpisodeViewModel(application, season);
        }
    }

    private LiveData<List<Episode>> episodesLiveData;

    public EpisodeViewModel(@NonNull Application application, Season season) {
        super(application, AppDatabase.getFileDatabase(application).episodeDao());
        episodesLiveData = dao.getEpisodes(season.getId());
    }

    public LiveData<List<Episode>> getEpisodesList() {
        return episodesLiveData;
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}
