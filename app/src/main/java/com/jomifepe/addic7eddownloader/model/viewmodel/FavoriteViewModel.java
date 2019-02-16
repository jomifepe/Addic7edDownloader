package com.jomifepe.addic7eddownloader.model.viewmodel;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.jomifepe.addic7eddownloader.model.Favorite;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.model.persistence.AppDatabase;
import com.jomifepe.addic7eddownloader.model.persistence.FavoriteDao;

import org.parceler.Parcel;

import java.util.List;

public class FavoriteViewModel extends BaseViewModel<Favorite, FavoriteDao> {
    private LiveData<List<Show>> favoritesShowsLiveData;

    public FavoriteViewModel(@NonNull Application application) {
        super(application, AppDatabase.getFileDatabase(application).favoriteDao());
        favoritesShowsLiveData = dao.getShows();
    }

    public LiveData<List<Show>> getFavorites() {
        return favoritesShowsLiveData;
    }

    public void deleteAll() {
        dao.deleteAll();
    }

    public void addShow(Show show) {
        Favorite lastFavorite = dao.getLast();
        Integer position = lastFavorite != null ? lastFavorite.getPosition() : 0;
        dao.insert(new Favorite(show.getAddic7edId(), position));
    }

    public void deleteShowById(Integer addic7edShowId) {
        dao.deleteById(addic7edShowId);
    }

    public boolean isShowOnFavorites(Integer addic7edShowId) {
        return dao.getByAddic7edId(addic7edShowId) != null;
    }
}
