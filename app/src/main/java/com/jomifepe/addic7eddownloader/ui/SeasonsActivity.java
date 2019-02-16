package com.jomifepe.addic7eddownloader.ui;

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
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.api.Addic7ed;
import com.jomifepe.addic7eddownloader.model.viewmodel.SeasonViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.ui.adapter.SeasonsRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.Const;
import com.jomifepe.addic7eddownloader.util.Util;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;

public class SeasonsActivity extends BaseActivity {
    public static final String EXTRA_TVSHOW = "com.jomifepe.addic7eddownloader.ui.TVSHOW";
    public static final String EXTRA_TVSHOW_SEASON = "com.jomifepe.addic7eddownloader.ui.TVSHOW_SEASON";

    @BindView(R.id.activity_seasons_progressBar) ProgressBar progressBar;
    @BindView(R.id.activity_seasons_listSeasons) RecyclerView listSeasons;

    private Show show;
    private SeasonViewModel seasonViewModel;

    private SeasonsRecyclerAdapter listSeasonsAdapter;
    private LinearLayoutManager listLayoutManager;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_seasons;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        show = Parcels.unwrap(getIntent().getParcelableExtra(Const.Activity.EXTRA_SHOW));
        setTitle(show.getTitle());

        listSeasonsAdapter = new SeasonsRecyclerAdapter(listSeasonsItemClickListener);
        listSeasons.setAdapter(listSeasonsAdapter);
        listSeasons.setLayoutManager(listLayoutManager = new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                listSeasons.getContext(), listLayoutManager.getOrientation());
        listSeasons.addItemDecoration(dividerItemDecoration);

        seasonViewModel = ViewModelProviders.of(this, new SeasonViewModel
                .SeasonViewModelFactory(getApplication(), show))
                .get(SeasonViewModel.class);

        observeSeasonsViewModel();
        loadSeasons();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    RecyclerViewItemShortClick listSeasonsItemClickListener = new RecyclerViewItemShortClick() {
        @Override
        public void onItemShortClick(View v, int position) {
            Season selectedSeason = listSeasonsAdapter.getList().get(position);

            Intent episodeActivityIntent = new Intent(SeasonsActivity.this, EpisodesActivity.class);
            episodeActivityIntent.putExtra(EXTRA_TVSHOW, Parcels.wrap(show));
            episodeActivityIntent.putExtra(EXTRA_TVSHOW_SEASON, Parcels.wrap(selectedSeason));
            startActivity(episodeActivityIntent);
        }
    };

    private void observeSeasonsViewModel() {
        seasonViewModel.getSeasonsList().observe(this, seasons -> {
            if (seasons != null) {
                listSeasonsAdapter.setList(new ArrayList<>(seasons));
            }
        });
    }

    private void loadSeasons() {
        new Addic7ed.ShowSeasonsRequest(show,
            results -> {
                ArrayList<Season> listSubtraction = new ArrayList<>(results);
                listSubtraction.removeAll(listSeasonsAdapter.getList());
                if (listSubtraction.size() > 0) {
                    Util.Async.Task dbTask = new Util.Async.Task(() -> {
                        seasonViewModel.insert(listSubtraction);
                    });
                    dbTask.addOnFailureListener(e -> handleException(e, R.string.error_persist_seasons));
                    dbTask.addOnCompleteListener(() -> progressBar.setVisibility(View.GONE));
                    dbTask.execute();
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            },
            e -> handleException(e, R.string.error_load_seasons))
            .execute();
    }
}
