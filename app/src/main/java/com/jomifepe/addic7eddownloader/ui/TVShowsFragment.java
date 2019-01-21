package com.jomifepe.addic7eddownloader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jomifepe.addic7eddownloader.Addic7ed;
import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.TVShow;
import com.jomifepe.addic7eddownloader.model.viewmodel.TVShowViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.RecyclerViewItemClick;
import com.jomifepe.addic7eddownloader.ui.adapter.TVShowsRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.AsyncUtil;
import com.jomifepe.addic7eddownloader.util.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVShowsFragment extends BaseFragment {
    public static final String EXTRA_TVSHOW = "com.jomifepe.addic7eddownloader.ui.TVSHOW";

    @BindView(R.id.listTVShows) RecyclerView listTVShows;
    @BindView(R.id.editTextFilter) EditText etxtFilter;
    @BindView(R.id.progressBarListTVShows) ProgressBar progressBarListTVShows;

    private TVShowViewModel tvShowViewModel;
    private TVShowsRecyclerAdapter listTVShowsRecyclerAdapter;

    public TVShowsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tvshows, container, false);
        ButterKnife.bind(this, view);

        listTVShowsRecyclerAdapter = new TVShowsRecyclerAdapter(tvShowsListItemClickListener);
        listTVShows.setAdapter(listTVShowsRecyclerAdapter);
        listTVShows.setLayoutManager(new LinearLayoutManager(getViewContext()));

        etxtFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listTVShowsRecyclerAdapter.getFilter().filter(charSequence);
            }

            @Override public void afterTextChanged(Editable editable) {}
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        tvShowViewModel = ViewModelProviders.of(this).get(TVShowViewModel.class);

        observeTVShowsViewModel();
        loadTVShows();
        return view;
    }

    private void observeTVShowsViewModel() {
        tvShowViewModel.getTvShowsList().observe(this, tvShows -> {
            if (tvShows != null) {
                listTVShowsRecyclerAdapter.setList(new ArrayList<>(tvShows));
            }
        });
    }

    private void loadTVShows() {
        Addic7ed.getTVShows(new Addic7ed.RecordResultListener<TVShow>() {
            @Override
            public void onComplete(List<TVShow> tvShows) {
                try {
                    ArrayList<TVShow> listSubtraction = new ArrayList<>(tvShows);
                    listSubtraction.removeAll(listTVShowsRecyclerAdapter.getList());
                    if (listSubtraction.size() > 0) {
                        AsyncUtil.RunnableAsyncTask dbTask = new AsyncUtil.RunnableAsyncTask(() -> {
                            tvShowViewModel.insert(tvShows);
                        });
                        dbTask.addOnFailureListener(e -> handleException(e, R.string.error_message_persist_tv_shows));
                        dbTask.addOnCompleteListener(() -> {
                            showMessage(listSubtraction.size() + " new TV Shows were added to the list");
                            progressBarListTVShows.setVisibility(View.GONE);
                        });
                        dbTask.execute();
                    } else {
                        runOnUiThread(() -> progressBarListTVShows.setVisibility(View.GONE));
                    }
                } catch (Exception e) {
                    handleException(e, R.string.error_message_load_tv_shows);
                }
            }

            @Override
            public void onFailure(Exception e) {
                handleException(e, R.string.error_message_failed_load_tv_shows);
            }
        });
    }

    RecyclerViewItemClick tvShowsListItemClickListener = new RecyclerViewItemClick() {
        @Override
        public void onItemClick(View v, int position) {
            TVShow show = listTVShowsRecyclerAdapter.getList().get(position);

            Intent tvShowActivityIntent = new Intent(getActivity(), TVShowSeasonsActivity.class);
            tvShowActivityIntent.putExtra(EXTRA_TVSHOW, show);
            startActivity(tvShowActivityIntent);
        }
    };
}
