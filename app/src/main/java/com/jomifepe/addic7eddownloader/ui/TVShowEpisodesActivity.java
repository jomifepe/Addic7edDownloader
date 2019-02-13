package com.jomifepe.addic7eddownloader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.jomifepe.addic7eddownloader.Addic7ed;
import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.model.viewmodel.EpisodeViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.EpisodesRecyclerAdapter;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;


public class TVShowEpisodesActivity extends BaseActivity {
    public static final String EXTRA_TVSHOW = "com.jomifepe.addic7eddownloader.ui.TVSHOW";
    public static final String EXTRA_TVSHOW_EPISODE = "com.jomifepe.addic7eddownloader.ui.TVSHOW_EPISODE";

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
        show = intent.getParcelableExtra(TVShowSeasonsActivity.EXTRA_TVSHOW);
        season = intent.getParcelableExtra(TVShowSeasonsActivity.EXTRA_TVSHOW_SEASON);

        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
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
        Addic7ed.getSeasonEpisodes(show, season, new Addic7ed.RecordResultListener<Episode>() {
            @Override
            public void onComplete(List<Episode> episodes) {
                try {
                    ArrayList<Episode> listSubtraction = new ArrayList<>(episodes);
                    listSubtraction.removeAll(listEpisodesAdapter.getList());
                    if (listSubtraction.size() > 0) {
                        Util.Async.RunnableTask dbTask = new Util.Async.RunnableTask(() ->
                                episodeViewModel.insert(listSubtraction));
                        dbTask.addOnFailureListener(e ->
                                handleException(e, R.string.error_persist_episodes));
                        dbTask.addOnCompleteListener(() ->
                                progressBar.setVisibility(View.GONE));
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
                handleException(e, R.string.error_load_episodes);
            }
        });
    }

    RecyclerViewItemShortClick listEpisodesItemClickListener = new RecyclerViewItemShortClick() {
        @Override
        public void onItemShortClick(View v, int position) {
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
}
