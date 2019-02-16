package com.jomifepe.addic7eddownloader.ui;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.api.Addic7ed;
import com.jomifepe.addic7eddownloader.model.viewmodel.EpisodeViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.EpisodesRecyclerAdapter;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.util.Const;
import com.jomifepe.addic7eddownloader.util.Util;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;


public class EpisodesActivity extends BaseActivity {
    @BindView(R.id.activity_episodes_listEpisodes) RecyclerView listEpisodes;
    @BindView(R.id.activity_episodes_progressBar) ProgressBar progressBar;

    private Show show;
    private Season season;

    private EpisodeViewModel episodeViewModel;
    private EpisodesRecyclerAdapter listEpisodesAdapter;
    private LinearLayoutManager listLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        show = Parcels.unwrap(getIntent().getParcelableExtra(SeasonsActivity.EXTRA_TVSHOW));
        season = Parcels.unwrap(getIntent().getParcelableExtra(SeasonsActivity.EXTRA_TVSHOW_SEASON));

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(show.getTitle());
        supportActionBar.setSubtitle(String.format(Locale.getDefault(), "Season %d", season.getNumber()));

        listEpisodesAdapter = new EpisodesRecyclerAdapter(listEpisodesItemClickListener);
        listEpisodes.setAdapter(listEpisodesAdapter);
        listEpisodes.setLayoutManager(listLayoutManager = new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                listEpisodes.getContext(), listLayoutManager.getOrientation());
        listEpisodes.addItemDecoration(dividerItemDecoration);

        episodeViewModel = ViewModelProviders.of(this, new EpisodeViewModel
                .EpisodeViewModelFactory(getApplication(), season))
                .get(EpisodeViewModel.class);

        observeEpisodesViewModel();
        loadEpisodes();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_episodes;
    }

    private void observeEpisodesViewModel() {
        episodeViewModel.getEpisodesList().observe(this, episodes -> {
            if (episodes != null) {
                listEpisodesAdapter.setList(new ArrayList<>(episodes));
            }
        });
    }

    private void loadEpisodes() {
        new Addic7ed.SeasonEpisodesRequest(show, season, results -> {
            try {
                ArrayList<Episode> listSubtraction = new ArrayList<>(results);
                listSubtraction.removeAll(listEpisodesAdapter.getList());
                if (listSubtraction.size() > 0) {
                    Util.Async.Task dbTask = new Util.Async.Task(() ->
                            episodeViewModel.insert(listSubtraction));
                    dbTask.addOnFailureListener(e ->
                            handleException(e, R.string.error_persist_episodes));
                    dbTask.addOnCompleteListener(() ->
                            progressBar.setVisibility(View.GONE));
                    dbTask.execute();
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                handleException(e, R.string.error_load_episodes);
            }
        }, e -> handleException(e, R.string.error_load_episodes))
        .execute();
    }

    RecyclerViewItemShortClick listEpisodesItemClickListener = new RecyclerViewItemShortClick() {
        @Override
        public void onItemShortClick(View v, int position) {
            Episode episode = listEpisodesAdapter.getItem(position);

            Intent episodeActivityIntent = new Intent(EpisodesActivity.this, SubtitlesActivity.class);
            episodeActivityIntent.putExtra(Const.Activity.EXTRA_MEDIA, Parcels.wrap(show));
            episodeActivityIntent.putExtra(Const.Activity.EXTRA_CONTENT, Parcels.wrap(episode));

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
