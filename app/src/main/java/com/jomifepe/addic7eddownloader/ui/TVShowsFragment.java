package com.jomifepe.addic7eddownloader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jomifepe.addic7eddownloader.Addic7ed;
import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.TVShow;
import com.jomifepe.addic7eddownloader.model.viewmodel.TVShowViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.EndlessRecyclerViewScrollListener;
import com.jomifepe.addic7eddownloader.ui.adapter.RecyclerViewItemClick;
import com.jomifepe.addic7eddownloader.ui.adapter.TVShowsRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.AsyncUtil;
import com.jomifepe.addic7eddownloader.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVShowsFragment extends BaseFragment {
    public static final String EXTRA_TVSHOW = "com.jomifepe.addic7eddownloader.ui.TVSHOW";

    @BindView(R.id.tvTVShows) RecyclerView rvTVShows;
    @BindView(R.id.etTVShowsFilter) EditText etFilter;
    @BindView(R.id.pbTVShows) ProgressBar progressBarTVShows;

    private TVShowViewModel tvShowViewModel;
    private TVShowsRecyclerAdapter listAdapter;
    private LinearLayoutManager listLayoutManager;
    private boolean isScrolling;

    public TVShowsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tvshows, container, false);
        ButterKnife.bind(this, view);

        rvTVShows.setAdapter(listAdapter = new TVShowsRecyclerAdapter(tvShowsListItemClickListener));
        rvTVShows.setLayoutManager(listLayoutManager = new LinearLayoutManager(getViewContext()));
        rvTVShows.addOnScrollListener(tvShowsScrollListener);

        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listAdapter.getFilter().filter(charSequence);
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
                listAdapter.setList(new ArrayList<>(tvShows));
            }
        });
    }

    RecyclerView.OnScrollListener tvShowsScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int currentItems = listLayoutManager.getChildCount();
            int totalItems = listLayoutManager.getItemCount();
            int scrollOutItems = listLayoutManager.findFirstVisibleItemPosition();

            LogUtil.logD(getViewContext(), "currentItems: " + currentItems +
                    ", scrollOutItems: " + scrollOutItems + ", totalItems: " + totalItems);
            if (isScrolling && (currentItems + scrollOutItems >= totalItems - 5)) {
                isScrolling = false;
                progressBarTVShows.setVisibility(View.VISIBLE);
                listAdapter.loadNewItems();
                progressBarTVShows.setVisibility(View.GONE);
            }
        }
    };

    private void loadTVShows() {
        Addic7ed.getTVShows(new Addic7ed.RecordResultListener<TVShow>() {
            @Override
            public void onComplete(List<TVShow> tvShows) {
                try {
                    ArrayList<TVShow> listSubtraction = new ArrayList<>(tvShows);
                    listSubtraction.removeAll(listAdapter.getList());
                    if (listSubtraction.size() > 0) {
                        AsyncUtil.RunnableAsyncTask dbTask = new AsyncUtil.RunnableAsyncTask(() -> {
                            tvShowViewModel.insert(tvShows);
                        });
                        dbTask.addOnFailureListener(e -> handleException(e, R.string.error_message_persist_tv_shows));
                        dbTask.addOnCompleteListener(() -> {
                            showMessage(listSubtraction.size() + " new TV Shows were added to the list");
                            progressBarTVShows.setVisibility(View.GONE);
                        });
                        dbTask.addOnTaskEndedListener(() -> progressBarTVShows.setVisibility(View.GONE));
                        dbTask.execute();
                    } else {
                        runOnUiThread(() -> progressBarTVShows.setVisibility(View.GONE));
                    }
                } catch (Exception e) {
                    handleException(e, R.string.error_message_load_tv_shows);
                    runOnUiThread(() -> progressBarTVShows.setVisibility(View.GONE));
                }
            }

            @Override
            public void onFailure(Exception e) {
                handleException(e, R.string.error_message_failed_load_tv_shows);
                runOnUiThread(() -> progressBarTVShows.setVisibility(View.GONE));
            }
        });
    }

    RecyclerViewItemClick tvShowsListItemClickListener = new RecyclerViewItemClick() {
        @Override
        public void onItemClick(View v, int position) {
            TVShow show = listAdapter.getList().get(position);

            Intent tvShowActivityIntent = new Intent(getActivity(), TVShowSeasonsActivity.class);
            tvShowActivityIntent.putExtra(EXTRA_TVSHOW, show);
            startActivity(tvShowActivityIntent);
        }
    };
}
