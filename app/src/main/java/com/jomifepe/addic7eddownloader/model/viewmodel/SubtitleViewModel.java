package com.jomifepe.addic7eddownloader.model.viewmodel;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.model.Subtitle;
import com.jomifepe.addic7eddownloader.model.persistence.AppDatabase;
import com.jomifepe.addic7eddownloader.model.persistence.SubtitleDao;

import java.util.List;

public class SubtitleViewModel extends BaseViewModel<Subtitle, SubtitleDao> {
    public static class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application application;
        private Episode episode;

        public ViewModelFactory(Application application, Episode episode) {
            this.episode = episode;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new SubtitleViewModel(application, episode);
        }
    }

    private LiveData<List<Subtitle>> subtitlesLiveData;

    public SubtitleViewModel(@NonNull Application application, Episode episode) {
        super(application, AppDatabase.getFileDatabase(application).subtitleDao());
        subtitlesLiveData = dao.getSubtitles(episode.getId());
    }

    public LiveData<List<Subtitle>> getSubtitlesList() {
        return subtitlesLiveData;
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}
