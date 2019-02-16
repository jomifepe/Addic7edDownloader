package com.jomifepe.addic7eddownloader.model.persistence;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.model.Favorite;
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.model.Subtitle;
import com.jomifepe.addic7eddownloader.model.persistence.typeconverter.MediaTypeConverter;
import com.jomifepe.addic7eddownloader.util.Const;

@Database(version = 1, exportSchema = false, entities = {
        Show.class,
        Season.class,
        Episode.class,
        Subtitle.class,
        Favorite.class
})
@TypeConverters({
        MediaTypeConverter.class
})

public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract TVShowDao tvShowDao();
    public abstract SeasonDao seasonDao();
    public abstract EpisodeDao episodeDao();
    public abstract SubtitleDao subtitleDao();
    public abstract FavoriteDao favoriteDao();

    public static AppDatabase getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                    AppDatabase.class).build();
        }
        return INSTANCE;
    }

    public static AppDatabase getFileDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, Const.DATABASE_FILENAME).build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
