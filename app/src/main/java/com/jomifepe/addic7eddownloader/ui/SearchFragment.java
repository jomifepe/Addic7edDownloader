package com.jomifepe.addic7eddownloader.ui;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.SearchResult;
import com.jomifepe.addic7eddownloader.api.Addic7ed;
import com.jomifepe.addic7eddownloader.ui.adapter.SearchResultsRecyclerAdapter;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.util.Const;

import org.parceler.Parcels;

public class SearchFragment extends BaseFragment {
    @BindView(R.id.et_search_field) TextInputEditText etSearchField;
    @BindView(R.id.rv_search_list) RecyclerView rvSearchResults;
    @BindView(R.id.tv_search_no_results) TextView tvNoResults;
    @BindView(R.id.pb_search) ProgressBar progressBar;

    private SearchResultsRecyclerAdapter listAdapter;
    private LinearLayoutManager listLayoutManager;

    public SearchFragment() {}

    @Override
    protected void onCreateViewActions(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        etSearchField.setOnEditorActionListener(searchFieldActionListener);
        listAdapter = new SearchResultsRecyclerAdapter(searchResultShortClick);
        rvSearchResults.setLayoutManager(listLayoutManager =
                new LinearLayoutManager(getViewContext()));
        rvSearchResults.setAdapter(listAdapter);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
//                rvSearchResults.getContext(), listLayoutManager.getOrientation());
//        rvSearchResults.addItemDecoration(dividerItemDecoration);

        onFragmentLoad();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_search;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.mi_main_shows_filter);
        menuItem.setVisible(false);
    }

    RecyclerViewItemShortClick searchResultShortClick = (view, position) -> {
        SearchResult result = listAdapter.getItem(position);
        Intent episodeActivityIntent = new Intent(getViewContext(), SubtitlesActivity.class);
        episodeActivityIntent.putExtra(Const.Activity.EXTRA_CONTENT, Parcels.wrap(result));

        startActivity(episodeActivityIntent);
    };

    EditText.OnEditorActionListener searchFieldActionListener = (view, actionId, event) -> {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String query = view.getText().toString().trim();
            if (!query.isEmpty()) performSearch(query);
            return true;
        }

        return false;
    };

    private void performSearch(String query) {
        listAdapter.clearList();
        tvNoResults.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new Addic7ed.SearchRequest(query,
            results -> {
                listAdapter.setList(results);
                progressBar.setVisibility(View.GONE);
                if (results.isEmpty()) {
                    tvNoResults.setVisibility(View.VISIBLE);
                }
            }, e -> {
                handleException(e, R.string.error_search_addic7ed);
                progressBar.setVisibility(View.GONE);
                tvNoResults.setVisibility(View.VISIBLE);
            })
        .execute();
    }
}
