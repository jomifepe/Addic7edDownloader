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
import com.jomifepe.addic7eddownloader.util.AsyncUtil;
import com.jomifepe.addic7eddownloader.util.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVShowSeasonsActivity extends BaseActivity {
    public static final String EXTRA_TVSHOW = "com.jomifepe.addic7eddownloader.ui.TVSHOW";
    public static final String EXTRA_TVSHOW_SEASON = "com.jomifepe.addic7eddownloader.ui.TVSHOW_SEASON";

    @BindView(R.id.activity_seasons_progressBar) ProgressBar progressBar;
    @BindView(R.id.activity_seasons_listSeasons) RecyclerView listSeasons;

    private TVShow show;
    private SeasonViewModel seasonViewModel;

    private SeasonsRecyclerAdapter listSeasonsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        show = intent.getParcelableExtra(TVShowsFragment.EXTRA_TVSHOW);
        setTitle(show.getTitle());

        listSeasonsAdapter = new SeasonsRecyclerAdapter(listSeasonsItemClickListener);
        listSeasons.setAdapter(listSeasonsAdapter);
        listSeasons.setLayoutManager(new LinearLayoutManager(this));

        seasonViewModel = ViewModelProviders.of(this,
                new SeasonViewModel.SeasonViewModelFactory(getApplication(), show)).get(SeasonViewModel.class);

        observeSeasonsViewModel();
        loadSeasons();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_seasons;
    }

    private void observeSeasonsViewModel() {
        seasonViewModel.getSeasonsList().observe(this, seasons -> {
            if (seasons != null) {
                listSeasonsAdapter.setList(new ArrayList<>(seasons));
            }
        });
    }

    private void loadSeasons() {
        Addic7ed.getTVShowSeasons(show, new Addic7ed.RecordResultListener<Season>() {
            @Override
            public void onComplete(List<Season> seasons) {
                try {
                    ArrayList<Season> listSubtraction = new ArrayList<>(seasons);
                    listSubtraction.removeAll(listSeasonsAdapter.getList());
                    if (listSubtraction.size() > 0) {
                        AsyncUtil.RunnableAsyncTask dbTask = new AsyncUtil.RunnableAsyncTask(() -> {
                            seasonViewModel.insert(listSubtraction);
                        });
                        dbTask.addOnFailureListener(e -> handleException(e, R.string.error_message_persist_seasons));
                        dbTask.addOnCompleteListener(() -> progressBar.setVisibility(View.GONE));
                        dbTask.execute();
                    } else {
                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    }
                } catch (Exception e) {
                    onFailure(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                handleException(e, R.string.error_message_failed_load_seasons);
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
}
