package com.jomifepe.addic7eddownloader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jomifepe.addic7eddownloader.Addic7ed;
import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.TVShow;
import com.jomifepe.addic7eddownloader.model.viewmodel.TVShowViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.RecyclerViewItemClickListener;
import com.jomifepe.addic7eddownloader.ui.adapter.TVShowsRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.Util;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.Toast;

public class TVShowsFragment extends Fragment {

    private final String TAG = TVShowsFragment.class.getSimpleName();
    private static final String PACKAGE_NAME = "com.jomifepe.addic7eddownloader.ui";
    public static final String EXTRA_TVSHOW = String.format("%s.TVSHOW", PACKAGE_NAME);
    private final int ADDIC7ED_LOADER_ID = Util.RANDOM.nextInt();

    private TVShowViewModel tvShowViewModel;
    private TVShowsRecyclerAdapter listTVShowsRecyclerAdapter;

    @BindView(R.id.listTVShows) RecyclerView listTVShows;
    @BindView(R.id.editTextFilter) EditText etxtFilter;
    @BindView(R.id.progressBarListTVShows) ProgressBar progressBarListTVShows;

    public TVShowsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tvshows, container, false);
        ButterKnife.bind(this, view);

        listTVShowsRecyclerAdapter = new TVShowsRecyclerAdapter(tvShowsListItemClickListener);
        listTVShows.setAdapter(listTVShowsRecyclerAdapter);
        listTVShows.setLayoutManager(new LinearLayoutManager(getContext()));

        etxtFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listTVShowsRecyclerAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}

            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        tvShowViewModel = ViewModelProviders.of(this).get(TVShowViewModel.class);

        observeTVShowsViewModel();

        getLoaderManager().initLoader(ADDIC7ED_LOADER_ID, null, addic7edLoaderListener).forceLoad();
        return view;
    }

    private void observeTVShowsViewModel() {
        tvShowViewModel.getTvShowsList().observe(this, tvShows -> {
            if (tvShows != null) {
                listTVShowsRecyclerAdapter.setList(new ArrayList<>(tvShows));
            }
        });
    }

    RecyclerViewItemClickListener tvShowsListItemClickListener = new RecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            TVShow show = listTVShowsRecyclerAdapter.getList().get(position);

            Intent tvShowActivityIntent = new Intent(getActivity(), TVShowSeasonsActivity.class);
            tvShowActivityIntent.putExtra(EXTRA_TVSHOW, show);
            startActivity(tvShowActivityIntent);
        }
    };

    private LoaderManager.LoaderCallbacks<ArrayList<TVShow>> addic7edLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<TVShow>>() {
        @NonNull
        @Override
        public Loader<ArrayList<TVShow>> onCreateLoader(int id, @Nullable Bundle args) {
            return new Addic7edTVShowsFetcher(getContext());
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList<TVShow>> loader, ArrayList<TVShow> fetchedShows) {
            ArrayList<TVShow> listSubtraction = new ArrayList<>(fetchedShows);
            listSubtraction.removeAll(listTVShowsRecyclerAdapter.getList());
            if (listSubtraction.size() > 0) {

                Util.RunnableAsyncTask dbTask = new Util.RunnableAsyncTask(() ->
                        tvShowViewModel.insert(listSubtraction),
                        ex -> {
                            Log.d(this.getClass().getSimpleName(), ex.getMessage(), ex);
                            Toast.makeText(getContext(), R.string.message_addic7edLoader_error, Toast.LENGTH_LONG).show();
                        }
                );
                dbTask.execute();
            }
            progressBarListTVShows.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<TVShow>> loader) {}
    };

    private static class Addic7edTVShowsFetcher extends AsyncTaskLoader<ArrayList<TVShow>> {
        Addic7edTVShowsFetcher(Context context) {
            super(context);
            onContentChanged();
        }

        @Override
        public ArrayList<TVShow> loadInBackground() {
            return Addic7ed.getTVShows();
        }
    }
}
