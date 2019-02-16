package com.jomifepe.addic7eddownloader.ui;


import android.app.Dialog;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.model.viewmodel.FavoriteViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.FavoritesRecyclerAdapter;
import com.jomifepe.addic7eddownloader.ui.adapter.FavoritesRecyclerItemTouchHelper;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.util.Const;
import com.jomifepe.addic7eddownloader.util.Util;
import com.jomifepe.addic7eddownloader.util.listener.OnCompleteListener;
import com.jomifepe.addic7eddownloader.util.listener.OnFailureListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;

public class FavoritesFragment extends BaseFragment {
    @BindView(R.id.rv_favorites) RecyclerView rvFavorites;
    @BindView(R.id.tv_favorites_empty_msg) TextView tvEmptyListMsg;

    private FavoriteViewModel favoritesViewModel;
    private FavoritesRecyclerAdapter listAdapter;
    private LinearLayoutManager listLayoutManager;
    private boolean isScrolling;

    public FavoritesFragment() {}

    @Override
    protected void onCreateViewActions(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        listAdapter = new FavoritesRecyclerAdapter(favoriteShortClick);
        rvFavorites.setAdapter(listAdapter);
        rvFavorites.setLayoutManager(listLayoutManager = new LinearLayoutManager(getViewContext()));
        rvFavorites.addOnScrollListener(favoritesScrollListener);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                rvFavorites.getContext(), listLayoutManager.getOrientation());
        rvFavorites.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new FavoritesRecyclerItemTouchHelper(0,
                        ItemTouchHelper.LEFT, listTouchListener);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvFavorites);

        favoritesViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);

        observeFavoritesViewModel();
        onFragmentLoad();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_favorites;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.mi_main_shows_filter);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.hint_filter_favorites));
        searchView.setQuery("", false);
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                listAdapter.getFilter().filter(query);
                return true;
            }
        });
    }

    private void observeFavoritesViewModel() {
        favoritesViewModel.getFavorites().observe(this, favorites -> {
            if (favorites != null) {
                listAdapter.setList(new ArrayList<>(favorites));
                tvEmptyListMsg.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    RecyclerViewItemShortClick favoriteShortClick = (view, position) -> {
        Show show = listAdapter.getList().get(position);
        Intent tvShowActivityIntent = new Intent(getActivity(), SeasonsActivity.class);
        tvShowActivityIntent.putExtra(Const.Activity.EXTRA_SHOW, Parcels.wrap(show));
        startActivity(tvShowActivityIntent);
    };

    RecyclerView.OnScrollListener favoritesScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true;
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int currentItems = listLayoutManager.getChildCount();
            int totalItems = listLayoutManager.getItemCount();
            int scrollOutItems = listLayoutManager.findFirstVisibleItemPosition();

            if (isScrolling && (currentItems + scrollOutItems >= totalItems - 5)) {
                isScrolling = false;
                listAdapter.loadNewItems();
            }
        }
    };

    FavoritesRecyclerItemTouchHelper.ItemTouchHelperListener listTouchListener =
        (viewHolder, direction, position) -> {
            if (!(viewHolder instanceof FavoritesRecyclerAdapter.ViewHolder)) return;

            Show selectedShow = listAdapter.getItem(position);
            Util.Dialog.createYesNoDialog(getViewContext(), R.string.msg_delete_favorite,
                (dialog, which) -> {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        removeShowFromFavorites(selectedShow,
                            () -> {
                                listAdapter.notifyItemRemoved(position);
                                shortMessage(R.string.msg_success_remove_favorites);
                            },
                            e -> handleException(e, R.string.error_remove_show_favorites));
                    } else {
                        listAdapter.notifyItemChanged(position);
                    }
                }).show();
    };

    private void removeShowFromFavorites(Show show, OnCompleteListener completeListener,
                                         OnFailureListener failureListener) {
        new Util.Async.Task(() -> favoritesViewModel.deleteShowById(show.getAddic7edId()))
                .addOnCompleteListener(completeListener)
                .addOnFailureListener(failureListener)
                .execute();
    }
}
