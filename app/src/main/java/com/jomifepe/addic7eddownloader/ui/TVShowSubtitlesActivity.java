package com.jomifepe.addic7eddownloader.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.ActionBar;
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
import com.jomifepe.addic7eddownloader.model.Subtitle;
import com.jomifepe.addic7eddownloader.model.TVShow;
import com.jomifepe.addic7eddownloader.model.viewmodel.SubtitleViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.RecyclerViewItemClickListener;
import com.jomifepe.addic7eddownloader.ui.adapter.TVShowSubtitlesRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.Const;
import com.jomifepe.addic7eddownloader.util.NetworkUtil;
import com.jomifepe.addic7eddownloader.util.Util;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class TVShowSubtitlesActivity
        extends AppCompatActivity {

    private TVShow show;
    private Episode episode;
    private Subtitle selectedSubtitle;

    private SubtitleViewModel subtitleViewModel;
    private TVShowSubtitlesRecyclerAdapter listSubtitlesAdapter;

    @BindView(R.id.activity_episode_substitles_listSubtitles) RecyclerView listSubtitles;
    @BindView(R.id.activity_episode_subtitles_progressBar) ProgressBar progressBar;

    private final int ADDIC7ED_LOADER_ID = Util.RANDOM.nextInt();
    private final int SUBTITLE_DOWDLOADER_LOADER_ID = Util.RANDOM.nextInt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_subtitles);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        show = intent.getParcelableExtra(TVShowEpisodesActivity.EXTRA_TVSHOW);
        episode = intent.getParcelableExtra(TVShowEpisodesActivity.EXTRA_TVSHOW_EPISODE);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(show.getTitle());
        supportActionBar.setSubtitle(String.format(Locale.getDefault(), "Season %d - Episode %d", episode.getSeason(), episode.getNumber()));

        listSubtitlesAdapter = new TVShowSubtitlesRecyclerAdapter(listSubtitlesItemClickListener);
        listSubtitles.setAdapter(listSubtitlesAdapter);
        listSubtitles.setLayoutManager(new LinearLayoutManager(this));

        subtitleViewModel = ViewModelProviders.of(this,
                new SubtitleViewModel.SubtitleViewModelFactory(getApplication(), episode)).get(SubtitleViewModel.class);

        observeSubtitlesViewModel();
        getSupportLoaderManager().initLoader(ADDIC7ED_LOADER_ID, null, addic7edLoaderListener).forceLoad();
    }

    private void observeSubtitlesViewModel() {
        subtitleViewModel.getSubtitlesList().observe(this, subtitles -> {
            if (subtitles != null) {
                listSubtitlesAdapter.setList(new ArrayList<>(subtitles));
            }
        });
    }

    RecyclerViewItemClickListener listSubtitlesItemClickListener = new RecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            selectedSubtitle = listSubtitlesAdapter.getItem(position);
            downloadSubtitle();
        }
    };

    @AfterPermissionGranted(Const.RC_WRITE_EXTERNAL_STORAGE)
    private void downloadSubtitle() {
        if (selectedSubtitle == null) {
            throw new IllegalStateException("No subtitle selected");
        }

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.write_external_storage_rationale),
                    Const.RC_WRITE_EXTERNAL_STORAGE, perms);
            return;
        }

        NetworkUtil.FileDownload fileDownload = new NetworkUtil.FileDownload(new NetworkUtil.NetworkTaskCallback() {
            @Override
            public void onTaskCompleted(String result) {
                Util.Notification notification = new Util.Notification(
                        TVShowSubtitlesActivity.this, Util.RANDOM.nextInt(), getString(R.string.subtitle_download_notification_title),
                        String.format(Locale.getDefault(), getString(R.string.subtitle_download_notification_message), result));
                notification.show();
            }

            @Override
            public void onTaskFailed(String result) {
                Toast.makeText(getApplicationContext(), R.string.message_network_error, Toast.LENGTH_LONG).show();
            }
        });

        fileDownload.execute(selectedSubtitle.getDownloadURL(), episode.getPageURL());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private LoaderManager.LoaderCallbacks<ArrayList<Subtitle>> addic7edLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<Subtitle>>() {

        @NonNull
        @Override
        public Loader<ArrayList<Subtitle>> onCreateLoader(int id, @Nullable Bundle args) {
            return new Addic7edSubtitlesFetcher(TVShowSubtitlesActivity.this, episode);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList<Subtitle>> loader, ArrayList<Subtitle> fetchedSeasons) {
            ArrayList<Subtitle> listSubtraction = new ArrayList<>(fetchedSeasons);
            listSubtraction.removeAll(listSubtitlesAdapter.getList());
            if (listSubtraction.size() > 0) {
                Util.RunnableAsyncTask dbTask = new Util.RunnableAsyncTask(() ->
                        subtitleViewModel.insert(listSubtraction),
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
        public void onLoaderReset(@NonNull Loader<ArrayList<Subtitle>> loader) {}
    };

//    private LoaderManager.LoaderCallbacks<Boolean> subtitleDownloaderLoaderListener = new LoaderManager.LoaderCallbacks<Boolean>() {
//
//        @NonNull
//        @Override
//        public Loader<Boolean> onCreateLoader(int id, @Nullable Bundle args) {
//            return new SubtitleDownloader(TVShowSubtitlesActivity.this, episode, selectedSubtitle);
//        }
//
//        @Override
//        public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean success) {
//            if (success) {
//                Util.Notification notification = new Util.Notification(TVShowSubtitlesActivity.this, Util.RANDOM.nextInt(),
//                        "Subtitle Download", "The subtitle was successfully downloaded");
//                notification.show();
//            } else {
//                Toast.makeText(getApplicationContext(), "Failed to download the subtitle", Toast.LENGTH_LONG).show();
//            }
//        }
//
//        @Override
//        public void onLoaderReset(@NonNull Loader<Boolean> loader) {}
//    };

    private static class Addic7edSubtitlesFetcher extends AsyncTaskLoader<ArrayList<Subtitle>> {
        private Episode episode;

        Addic7edSubtitlesFetcher(Context context, Episode episode) {
            super(context);
            this.episode = episode;
            onContentChanged();
        }

        @Override
        public ArrayList<Subtitle> loadInBackground() {
            return Addic7ed.getEpisodeSubtitles(episode);
        }
    }

//    private static class SubtitleDownloader extends AsyncTaskLoader<Boolean> {
//        Episode episode;
//        Subtitle subtitle;
//
//        public SubtitleDownloader(@NonNull Context context, Episode episode, Subtitle subtitle) {
//            super(context);
//            this.episode = episode;
//            this.subtitle = subtitle;
//        }
//
//        @Nullable
//        @Override
//        public Boolean loadInBackground() {
//
//
//            return true;
//        }
//    }
}
