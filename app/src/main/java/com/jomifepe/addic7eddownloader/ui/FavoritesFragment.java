package com.jomifepe.addic7eddownloader.ui;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemLongClick;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.util.Util;
import com.jomifepe.addic7eddownloader.util.listener.OnCompleteListener;
import com.jomifepe.addic7eddownloader.util.listener.OnFailureListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends BaseFragment {
    @BindView(R.id.rv_favorites) RecyclerView rvFavorites;
    @BindView(R.id.tv_favorites_empty_msg) TextView tvEmptyListMsg;

    private FavoriteViewModel favoritesViewModel;
    private FavoritesRecyclerAdapter listAdapter;
    private LinearLayoutManager listLayoutManager;
    private boolean isScrolling;

    public FavoritesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        listAdapter = new FavoritesRecyclerAdapter(favoriteShortClick, favoriteLongClick);
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
        return view;
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
        Intent tvShowActivityIntent = new Intent(getActivity(), TVShowSeasonsActivity.class);
        tvShowActivityIntent.putExtra(getString(R.string.intent_extra_tv_show), show);
        startActivity(tvShowActivityIntent);
    };

    RecyclerViewItemLongClick favoriteLongClick = (view, position) -> {
        Show selectedShow = listAdapter.getItem(position);
        removeShowFromFavorites(selectedShow,
                () -> Util.Message.sToast(getViewContext(), R.string.msg_success_remove_favorites),
                e -> handleException(e, R.string.error_remove_show_favorites));
        return true;
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
                        listAdapter.notifyItemRemoved(position);
                    }
                }).show();
    };

    private void removeShowFromFavorites(Show show, OnCompleteListener completeListener,
                                         OnFailureListener failureListener) {
        new Util.Async.RunnableTask(() -> favoritesViewModel.deleteShowById(show.getAddic7edId()))
                .addOnCompleteListener(completeListener)
                .addOnFailureListener(failureListener)
                .execute();
    }
}
