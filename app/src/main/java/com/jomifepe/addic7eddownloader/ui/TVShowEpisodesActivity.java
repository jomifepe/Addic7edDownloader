package com.jomifepe.addic7eddownloader.ui;

import android.app.ActionBar;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jomifepe.addic7eddownloader.Addic7ed;
import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.model.TVShow;
import com.jomifepe.addic7eddownloader.model.viewmodel.EpisodeViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.RecyclerViewItemClickListener;
import com.jomifepe.addic7eddownloader.ui.adapter.TVShowEpisodesRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.Util;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TVShowEpisodesActivity
        extends AppCompatActivity {

    public static final String PACKAGE_NAME = "com.jomifepe.addic7eddownloader.ui";
    public static final String EXTRA_TVSHOW = String.format("%s.TVSHOW", PACKAGE_NAME);
    public static final String EXTRA_TVSHOW_EPISODE = String.format("%s.TVSHOW_EPISODE", PACKAGE_NAME);
    private final int ADDIC7ED_LOADER_ID = Util.RANDOM.nextInt();

    private TVShow show;
    private Season season;

    private EpisodeViewModel episodeViewModel;
    private TVShowEpisodesRecyclerAdapter listEpisodesAdapter;

    @BindView(R.id.activity_episodes_listEpisodes) RecyclerView listEpisodes;
    @BindView(R.id.activity_episodes_progressBar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        show = intent.getParcelableExtra(TVShowSeasonsActivity.EXTRA_TVSHOW);
        season = intent.getParcelableExtra(TVShowSeasonsActivity.EXTRA_TVSHOW_SEASON);

        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(show.getTitle());
        supportActionBar.setSubtitle(String.format(Locale.getDefault(), "Season %d", season.getNumber()));

        listEpisodesAdapter = new TVShowEpisodesRecyclerAdapter(listEpisodesItemClickListener);
        listEpisodes.setAdapter(listEpisodesAdapter);
        listEpisodes.setLayoutManager(new LinearLayoutManager(this));

        episodeViewModel = ViewModelProviders.of(this,
                new EpisodeViewModel.EpisodeViewModelFactory(getApplication(), season)).get(EpisodeViewModel.class);

        observeEpisodesViewModel();
        getSupportLoaderManager().initLoader(ADDIC7ED_LOADER_ID, null, addic7edLoaderListener).forceLoad();
    }

    private void observeEpisodesViewModel() {
        episodeViewModel.getEpisodesList().observe(this, episodes -> {
            if (episodes != null) {
                listEpisodesAdapter.setList(new ArrayList<>(episodes));
            }
        });
    }

    RecyclerViewItemClickListener listEpisodesItemClickListener = new RecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Episode episode = listEpisodesAdapter.getItem(position);

            Intent episodeActivityIntent = new Intent(TVShowEpisodesActivity.this, TVShowSubtitlesActivity.class);
            episodeActivityIntent.putExtra(EXTRA_TVSHOW, show);
            episodeActivityIntent.putExtra(EXTRA_TVSHOW_EPISODE, episode);

            startActivity(episodeActivityIntent);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private LoaderManager.LoaderCallbacks<ArrayList<Episode>> addic7edLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<Episode>>() {
        @NonNull
        @Override
        public Loader<ArrayList<Episode>> onCreateLoader(int id, @Nullable Bundle args) {
            return new Addic7edEpisodesFetcher(TVShowEpisodesActivity.this, show, season);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList<Episode>> loader, ArrayList<Episode> fetchedEpisodes) {
            ArrayList<Episode> listSubtraction = new ArrayList<>(fetchedEpisodes);
            listSubtraction.removeAll(listEpisodesAdapter.getList());
            if (listSubtraction.size() > 0) {
                Util.RunnableAsyncTask dbTask = new Util.RunnableAsyncTask(() ->
                        episodeViewModel.insert(listSubtraction),
                        ex -> {
                            Log.d(this.getClass().getSimpleName(), ex.getMessage(), ex);
                            Toast.makeText(getApplicationContext(), R.string.message_addic7edLoader_error, Toast.LENGTH_LONG).show();
                        }
                );
                dbTask.execute();
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<Episode>> loader) {}
    };

    private static class Addic7edEpisodesFetcher extends AsyncTaskLoader<ArrayList<Episode>> {
        private TVShow show;
        private Season season;

        Addic7edEpisodesFetcher(Context context, TVShow show, Season season) {
            super(context);
            this.show = show;
            this.season = season;
            onContentChanged();
        }

        @Override
        public ArrayList<Episode> loadInBackground() {
            return Addic7ed.getSeasonEpisodes(show, season);
        }
    }
}
