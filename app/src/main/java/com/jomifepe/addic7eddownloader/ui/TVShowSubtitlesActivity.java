package com.jomifepe.addic7eddownloader.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
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
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.model.Subtitle;
import com.jomifepe.addic7eddownloader.model.viewmodel.SubtitleViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.ui.adapter.SubtitlesRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.Const;
import com.jomifepe.addic7eddownloader.util.NetworkUtil;
import com.jomifepe.addic7eddownloader.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class TVShowSubtitlesActivity extends BaseActivity {
    @BindView(R.id.activity_episode_substitles_listSubtitles) RecyclerView listSubtitles;
    @BindView(R.id.activity_episode_subtitles_progressBar) ProgressBar progressBar;

    private Show show;
    private Episode episode;
    private Subtitle selectedSubtitle;

    private SubtitleViewModel subtitleViewModel;
    private SubtitlesRecyclerAdapter listSubtitlesAdapter;
    private LinearLayoutManager listLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        show = intent.getParcelableExtra(TVShowEpisodesActivity.EXTRA_TVSHOW);
        episode = intent.getParcelableExtra(TVShowEpisodesActivity.EXTRA_TVSHOW_EPISODE);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(show.getTitle());
        supportActionBar.setSubtitle(String.format(Locale.getDefault(), "Season %d - Episode %d", episode.getSeason(), episode.getNumber()));

        listSubtitlesAdapter = new SubtitlesRecyclerAdapter(listSubtitlesItemClickListener);
        listSubtitles.setAdapter(listSubtitlesAdapter);
        listSubtitles.setLayoutManager(listLayoutManager = new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                listSubtitles.getContext(), listLayoutManager.getOrientation());
        listSubtitles.addItemDecoration(dividerItemDecoration);

        subtitleViewModel = ViewModelProviders.of(this,
                new SubtitleViewModel.SubtitleViewModelFactory(getApplication(), episode)).get(SubtitleViewModel.class);

        observeSubtitlesViewModel();
        loadSubtitles();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_episode_subtitles;
    }

    private void observeSubtitlesViewModel() {
        subtitleViewModel.getSubtitlesList().observe(this, subtitles -> {
            if (subtitles != null) {
                listSubtitlesAdapter.setList(new ArrayList<>(subtitles));
            }
        });
    }

    private void loadSubtitles() {
        Addic7ed.getEpisodeSubtitles(episode, new Addic7ed.RecordResultListener<Subtitle>() {
            @Override
            public void onComplete(List<Subtitle> subtitles) {
                try {
                    ArrayList<Subtitle> listSubtraction = new ArrayList<>(subtitles);
                    listSubtraction.removeAll(listSubtitlesAdapter.getList());
                    if (listSubtraction.size() > 0) {
                        Util.Async.RunnableTask dbTask = new Util.Async.RunnableTask(() -> {
                            subtitleViewModel.insert(listSubtraction);
                        });
                        dbTask.addOnFailureListener(e ->
                                handleException(e, R.string.error_persist_subtitles));
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
                handleException(e, R.string.error_load_subtitles);
            }
        });
    }

    RecyclerViewItemShortClick listSubtitlesItemClickListener = new RecyclerViewItemShortClick() {
        @Override
        public void onItemShortClick(View v, int position) {
            selectedSubtitle = listSubtitlesAdapter.getItem(position);
            downloadSubtitle();
        }
    };

    @AfterPermissionGranted(Const.RC_WRITE_EXTERNAL_STORAGE)
    private void downloadSubtitle() {
        if (selectedSubtitle == null) {
            throw new IllegalStateException("No subtitle selected");
        }

        String perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!EasyPermissions.hasPermissions(this, perm)) {
            EasyPermissions.requestPermissions(this, getString(R.string.write_external_storage_rationale),
                    Const.RC_WRITE_EXTERNAL_STORAGE, perm);
            return;
        }

        String savePath = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(R.string.key_pref_subtitle_save_location), null);
        if (savePath == null) {
            savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getAbsolutePath();
        }

        NetworkUtil.FileDownload fileDownload = new NetworkUtil.FileDownload(
                selectedSubtitle.getDownloadURL(), episode.getPageURL(), savePath);
        fileDownload.addOnFailureListener(e -> handleException(e, R.string.error_download_subtitle));
        fileDownload.addOnCompleteListener(filename -> {
            Util.Notification notification = new Util.Notification(this,
                    R.string.title_notification_subtitle_download, filename);
            notification.show();
        });
        fileDownload.execute();
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
}
