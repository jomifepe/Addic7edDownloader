package com.jomifepe.addic7eddownloader.ui;

import android.Manifest;
import androidx.lifecycle.ViewModelProviders;

import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Content;
import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.model.Media;
import com.jomifepe.addic7eddownloader.model.SearchResult;
import com.jomifepe.addic7eddownloader.model.Subtitle;
import com.jomifepe.addic7eddownloader.api.Addic7ed;
import com.jomifepe.addic7eddownloader.model.viewmodel.SubtitleViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.ui.adapter.SubtitlesRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.Const;
import com.jomifepe.addic7eddownloader.util.Util;
import com.jomifepe.addic7eddownloader.util.listener.OnResultListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SubtitlesActivity extends BaseActivity {
    @BindView(R.id.activity_episode_substitles_listSubtitles) RecyclerView rvSubtitles;
    @BindView(R.id.activity_episode_subtitles_progressBar) ProgressBar progressBar;

    private Media media;
    private Content content;
    private Subtitle selectedSubtitle;

    private SubtitleViewModel subtitleViewModel;
    private SubtitlesRecyclerAdapter listAdapter;
    private LinearLayoutManager listLayoutManager;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_subtitles;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        media = Parcels.unwrap(getIntent().getParcelableExtra(Const.Activity.EXTRA_MEDIA));
        content = Parcels.unwrap(getIntent().getParcelableExtra(Const.Activity.EXTRA_CONTENT));
        setupTitles();

        listAdapter = new SubtitlesRecyclerAdapter(listSubtitlesItemClickListener);
        rvSubtitles.setAdapter(listAdapter);
        rvSubtitles.setLayoutManager(listLayoutManager = new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                rvSubtitles.getContext(), listLayoutManager.getOrientation());
        rvSubtitles.addItemDecoration(dividerItemDecoration);

        if (content instanceof Episode) {
            subtitleViewModel = ViewModelProviders.of(this,
                    new SubtitleViewModel.ViewModelFactory(getApplication(), (Episode)content))
                    .get(SubtitleViewModel.class);
            observeSubtitlesViewModel();
        }

        loadSubtitles();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

    RecyclerViewItemShortClick listSubtitlesItemClickListener = new RecyclerViewItemShortClick() {
        @Override
        public void onItemShortClick(View v, int position) {
            selectedSubtitle = listAdapter.getItem(position);
            downloadSubtitle();
        }
    };

    private void observeSubtitlesViewModel() {
        subtitleViewModel.getSubtitlesList().observe(this, subtitles -> {
            if (subtitles != null) {
                listAdapter.setList(new ArrayList<>(subtitles));
            }
        });
    }

    private void setupTitles() {
        ActionBar supportActionBar = getSupportActionBar();
        if (media != null) {
            supportActionBar.setTitle(media.getTitle());
        } else {
            if (content instanceof SearchResult) {
                SearchResult searchResult = (SearchResult) this.content;
                supportActionBar.setTitle(searchResult.getType().getTitle(this));
                supportActionBar.setSubtitle(searchResult.getDescription());
            } else {
                Episode episode = (Episode) this.content;
                supportActionBar.setTitle(media.getTitle());
                final String actionBarSubtitle = String.format(Locale.getDefault(),
                        "Season %d - Episode %d", episode.getSeasonNumber(), episode.getNumber());
                supportActionBar.setSubtitle(actionBarSubtitle);
            }
        }
    }

    private void loadSubtitles() {
        new Addic7ed.ContentSubtitlesRequest(content, subtitlesRequestContentListener,
                e -> handleException(e, R.string.error_load_subtitles)).execute();
    }

    OnResultListener<List<Subtitle>> subtitlesRequestContentListener = results -> {
        try {
            if (content instanceof Episode) {
                ArrayList<Subtitle> listSubtraction = new ArrayList<>(results);
                listSubtraction.removeAll(listAdapter.getList());
                if (listSubtraction.size() > 0) {
                    new Util.Async.Task(() -> subtitleViewModel.insert(listSubtraction))
                        .addOnFailureListener(e -> handleException(e, R.string.error_persist_subtitles))
                        .addOnTaskEndedListener(() -> progressBar.setVisibility(View.GONE))
                        .execute();
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            } else {
                listAdapter.setList(results);
                progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            handleException(e, R.string.error_load_subtitles);
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

        new Util.Network.FileDownload(selectedSubtitle.getDownloadURL(), content.getPageUrl(), savePath)
            .addOnFailureListener(e -> handleException(e, R.string.error_download_subtitle))
            .addOnCompleteListener(filename -> {
                new Util.Notification(this, R.string.title_notification_subtitle_download, filename).show();
            })
            .execute();
    }
}
