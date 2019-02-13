package com.jomifepe.addic7eddownloader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.Addic7ed;
import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Favorite;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.model.persistence.AppDatabase;
import com.jomifepe.addic7eddownloader.model.viewmodel.FavoriteViewModel;
import com.jomifepe.addic7eddownloader.model.viewmodel.ShowViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemLongClick;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.ui.adapter.ShowsRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.Util;
import com.jomifepe.addic7eddownloader.util.listener.OnCompleteListener;
import com.jomifepe.addic7eddownloader.util.listener.OnFailureListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVShowsFragment
        extends BaseFragment {

    @BindView(R.id.rv_shows) RecyclerView listShows;
    @BindView(R.id.pb_shows) ProgressBar progressBarShows;
    @BindView(R.id.tv_shows_empty_msg) TextView tvEmptyListMsg;

    private ShowViewModel showsViewModel;
    private FavoriteViewModel favoritesViewModel;
    private ShowsRecyclerAdapter listAdapter;
    private LinearLayoutManager listLayoutManager;
    private Show selectedShowActiveMenu;
    private static boolean firstLoad;
    private boolean isScrolling;

    static {
        firstLoad = true;
    }

    public TVShowsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shows, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        listShows.setAdapter(listAdapter = new ShowsRecyclerAdapter(
                showShortClick, showLongClick));
        listShows.setLayoutManager(listLayoutManager = new LinearLayoutManager(getViewContext()));
        listShows.addOnScrollListener(tvShowsScrollListener);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                listShows.getContext(), listLayoutManager.getOrientation());
        listShows.addItemDecoration(dividerItemDecoration);

        showsViewModel = ViewModelProviders.of(this).get(ShowViewModel.class);
        favoritesViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);

        observeTVShowsViewModel();
        if (firstLoad) loadTVShows();
        onFragmentLoad();

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.mi_main_shows_filter);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.hint_filter_shows));
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

    private void observeTVShowsViewModel() {
        showsViewModel.getTvShowsList().observe(this, tvShows -> {
            if (tvShows != null) {
                listAdapter.setList(new ArrayList<>(tvShows));
                tvEmptyListMsg.setVisibility(tvShows.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void loadTVShows() {
        progressBarShows.setVisibility(View.VISIBLE);
        Addic7ed.getTVShows(new Addic7ed.RecordResultListener<Show>() {
            @Override
            public void onComplete(List<Show> shows) {
                try {
                    ArrayList<Show> listSubtraction = new ArrayList<>(shows);
                    listSubtraction.removeAll(listAdapter.getList());
                    if (listSubtraction.size() > 0) {
                        Util.Async.RunnableTask dbTask = new Util.Async.RunnableTask(() -> {
                            showsViewModel.insert(shows);
                        });
                        dbTask.addOnFailureListener(e -> {
                            View mainCoordinator = getActivity().findViewById(R.id.coordinator_main);
                            Snackbar.make(mainCoordinator, R.string.error_persist_shows, Snackbar.LENGTH_LONG).show();
//                            handleException(e, R.string.error_persist_shows);
                        });
                        dbTask.addOnCompleteListener(() -> {
                            longMessage(listSubtraction.size() + " new TV Shows were added to the list");
                            progressBarShows.setVisibility(View.GONE);
                        });
                        dbTask.addOnTaskEndedListener(() -> {
                            progressBarShows.setVisibility(View.GONE);
                            firstLoad = false;
                        });
                        dbTask.execute();
                    } else {
                        runOnUiThread(() -> progressBarShows.setVisibility(View.GONE));
                    }
                } catch (Exception e) {
                    handleException(e, R.string.error_default_load_shows);
                    runOnUiThread(() -> progressBarShows.setVisibility(View.GONE));
                }
            }

            @Override
            public void onFailure(Exception e) {
                handleException(e, R.string.error_load_shows);
                runOnUiThread(() -> progressBarShows.setVisibility(View.GONE));
            }
        });
    }

    RecyclerViewItemShortClick showShortClick = (view, position) -> {
        Show show = listAdapter.getList().get(position);
        Intent tvShowActivityIntent = new Intent(getActivity(), TVShowSeasonsActivity.class);
        tvShowActivityIntent.putExtra(getString(R.string.intent_extra_tv_show), show);
        startActivity(tvShowActivityIntent);
    };

    RecyclerViewItemLongClick showLongClick = (view, position) -> {
        selectedShowActiveMenu = listAdapter.getItem(position);

        checkIfShowIsOnFavorites(getViewContext(), selectedShowActiveMenu, isOnFavorites -> {
            final RecyclerViewItemShortClick onMenuOptionClick = (menuView, menuItemIndex) -> {
                if (menuItemIndex == 0) {
                    if (isOnFavorites) {
                        removeShowFromFavorites(selectedShowActiveMenu,
                            () -> shortMessage(R.string.msg_success_remove_favorites),
                            e -> handleException(e, R.string.error_remove_show_favorites));
                    } else {
                        addShowToFavorites(selectedShowActiveMenu,
                            () -> shortMessage(R.string.msg_success_add_favorites),
                            e -> handleException(e, R.string.error_add_show_favorites));
                    }
                }
            };

            ArrayList<Pair<Integer, String>> options = new ArrayList<>();
            if (isOnFavorites) {
                options.add(new Pair<>(R.drawable.ic_favorite_border_black_24dp,
                        getString(R.string.label_action_menu_remove_favorites)));
            } else {
                options.add(new Pair<>(R.drawable.ic_favorite_black_24dp,
                        getString(R.string.label_action_menu_add_favorites)));
            }
            AlertDialog menu = Util.Dialog.createMultiOptionMenu(getViewContext(),
                    selectedShowActiveMenu.getTitle(), options, onMenuOptionClick);
            menu.show();
        });
        return true;
    };

    RecyclerView.OnScrollListener tvShowsScrollListener = new RecyclerView.OnScrollListener() {
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
                progressBarShows.setVisibility(View.VISIBLE);
                listAdapter.loadNewItems();
                progressBarShows.setVisibility(View.GONE);
            }
        }
    };
    private void addShowToFavorites(Show show, OnCompleteListener completeListener,
                            OnFailureListener failureListener) {
        new Util.Async.RunnableTask(() -> favoritesViewModel.addShow(show))
            .addOnCompleteListener(completeListener)
            .addOnFailureListener(failureListener)
            .execute();
    }

    private void removeShowFromFavorites(Show show, OnCompleteListener completeListener,
                                         OnFailureListener failureListener) {
       new Util.Async.RunnableTask(() -> favoritesViewModel.deleteShowById(show.getAddic7edId()))
            .addOnCompleteListener(completeListener)
            .addOnFailureListener(failureListener)
            .execute();
    }

    void checkIfShowIsOnFavorites(Context context, Show show,
                                         FindShowOnFavorites.ResultListener listener) {
        new FindShowOnFavorites(context, show.getAddic7edId(), listener).execute();
    }

    static class FindShowOnFavorites extends AsyncTask<Void, Void, Boolean> {
        interface ResultListener {
            void onComplete(boolean isFavorite);
        }

        private final WeakReference<Context> context;
        private final ResultListener listener;
        private final Integer addic7edId;

        public FindShowOnFavorites(Context context, Integer addic7edId, ResultListener listener) {
            this.context = new WeakReference<>(context);
            this.listener = listener;
            this.addic7edId = addic7edId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                AppDatabase db = AppDatabase.getFileDatabase(context.get());
                Favorite favourite = db.favoriteDao().getByAddic7edId(addic7edId);
                return favourite != null;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            listener.onComplete(result);
        }
    }
}
