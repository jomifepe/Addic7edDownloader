package com.jomifepe.addic7eddownloader.ui;

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
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.model.TVShow;
import com.jomifepe.addic7eddownloader.model.viewmodel.SeasonViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.RecyclerViewItemClick;
import com.jomifepe.addic7eddownloader.ui.adapter.SeasonsRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVShowSeasonsActivity
        extends AppCompatActivity {

    public static final String PACKAGE_NAME = "com.jomifepe.addic7eddownloader.ui";
    public static final String EXTRA_TVSHOW = String.format("%s.TVSHOW", PACKAGE_NAME);
    public static final String EXTRA_TVSHOW_SEASON = String.format("%s.TVSHOW_SEASON", PACKAGE_NAME);
    private final int ADDIC7ED_LOADER_ID = Util.RANDOM.nextInt();

    private TVShow show;
    private SeasonViewModel seasonViewModel;

    private SeasonsRecyclerAdapter listSeasonsAdapter;

    @BindView(R.id.activity_seasons_listSeasons) RecyclerView listSeasons;
    @BindView(R.id.activity_seasons_progressBar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasons);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        show = intent.getParcelableExtra(TVShowsFragment.EXTRA_TVSHOW);
        setTitle(show.getTitle());

        listSeasonsAdapter = new SeasonsRecyclerAdapter(listSeasonsItemClickListener);
        listSeasons.setAdapter(listSeasonsAdapter);
        listSeasons.setLayoutManager(new LinearLayoutManager(this));

        seasonViewModel = ViewModelProviders.of(this,
                new SeasonViewModel.SeasonViewModelFactory(getApplication(), show)).get(SeasonViewModel.class);


        observeSeasonsViewModel();
        getSupportLoaderManager().initLoader(ADDIC7ED_LOADER_ID, null, addic7edLoaderListener).forceLoad();
    }

    private void observeSeasonsViewModel() {
        seasonViewModel.getSeasonsList().observe(this, seasons -> {
            if (seasons != null) {
                listSeasonsAdapter.setList(new ArrayList<>(seasons));
            }
        });
    }

    RecyclerViewItemClick listSeasonsItemClickListener = new RecyclerViewItemClick() {
        @Override
        public void onItemClick(View v, int position) {
            Season selectedSeason = listSeasonsAdapter.getList().get(position);

            Intent episodeActivityIntent = new Intent(TVShowSeasonsActivity.this, TVShowEpisodesActivity.class);
            episodeActivityIntent.putExtra(EXTRA_TVSHOW, show);
            episodeActivityIntent.putExtra(EXTRA_TVSHOW_SEASON, selectedSeason);
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

    private LoaderManager.LoaderCallbacks<ArrayList<Season>> addic7edLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<Season>>() {

        @NonNull
        @Override
        public Loader<ArrayList<Season>> onCreateLoader(int id, @Nullable Bundle args) {
            return new Addic7edSeasonsFetcher(TVShowSeasonsActivity.this, show);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList<Season>> loader, ArrayList<Season> fetchedSeasons) {
            ArrayList<Season> listSubtraction = new ArrayList<>(fetchedSeasons);
            listSubtraction.removeAll(listSeasonsAdapter.getList());
            if (listSubtraction.size() > 0) {
                Util.RunnableAsyncTask dbTask = new Util.RunnableAsyncTask(() ->
                        seasonViewModel.insert(listSubtraction),
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
        public void onLoaderReset(@NonNull Loader<ArrayList<Season>> loader) {}
    };

    private static class Addic7edSeasonsFetcher extends AsyncTaskLoader<ArrayList<Season>> {
        private TVShow show;

        Addic7edSeasonsFetcher(Context context, TVShow show) {
            super(context);
            this.show = show;
            onContentChanged();
        }

        @Override
        public ArrayList<Season> loadInBackground() {
            return Addic7ed.getTVShowSeasons(show);
        }
    }
}
