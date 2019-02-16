package com.jomifepe.addic7eddownloader.ui;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Favorite;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.api.Addic7ed;
import com.jomifepe.addic7eddownloader.model.persistence.AppDatabase;
import com.jomifepe.addic7eddownloader.model.viewmodel.FavoriteViewModel;
import com.jomifepe.addic7eddownloader.model.viewmodel.ShowViewModel;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemLongClick;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.ui.adapter.ShowsRecyclerAdapter;
import com.jomifepe.addic7eddownloader.util.Const;
import com.jomifepe.addic7eddownloader.util.Util;
import com.jomifepe.addic7eddownloader.util.listener.OnCompleteListener;
import com.jomifepe.addic7eddownloader.util.listener.OnFailureListener;

import org.parceler.Parcels;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;

public class ShowsFragment extends BaseFragment {

    @BindView(R.id.rv_shows) RecyclerView listShows;
    @BindView(R.id.pb_shows) ProgressBar progressBarShows;
    @BindView(R.id.tv_shows_empty_msg) TextView tvEmptyListMsg;

    private ShowViewModel showsViewModel;
    private FavoriteViewModel favoritesViewModel;
    private ShowsRecyclerAdapter listAdapter;
    private LinearLayoutManager listLayoutManager;
    private Show selectedShowActiveMenu;
    private boolean isScrolling;
    private static boolean firstLoad;

    static {
        firstLoad = true;
    }

    public ShowsFragment() {}

    @Override
    protected void onCreateViewActions(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
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
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_shows;
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

    RecyclerViewItemShortClick showShortClick = (view, position) -> {
        Show show = listAdapter.getList().get(position);
        Intent tvShowActivityIntent = new Intent(getActivity(), SeasonsActivity.class);
        tvShowActivityIntent.putExtra(Const.Activity.EXTRA_SHOW, Parcels.wrap(show));
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
        new Addic7ed.ShowsRequest(
            results -> {
                try {
                    ArrayList<Show> listSubtraction = new ArrayList<>(results);
                    listSubtraction.removeAll(listAdapter.getList());
                    if (listSubtraction.size() > 0) {
                        new Util.Async.Task(() -> showsViewModel.insert(results))
                                .addOnFailureListener(e -> longMessage(R.string.error_persist_shows))
                                .addOnCompleteListener(() -> longMessage(listSubtraction.size() +
                                " new TV ShowsRequest were added to the list"))
                                .addOnTaskEndedListener(() -> {
                                    setProgressBarVisibility(false);
                                    firstLoad = false;
                                })
                                .execute();
                    } else {
                        setProgressBarVisibility(false);
                    }
                } catch (Exception e) {
                    handleException(e, R.string.error_default_load_shows);
                    setProgressBarVisibility(false);
                }
            },
            e -> {
                handleException(e, R.string.error_load_shows);
                setProgressBarVisibility(false);
            })
        .execute();
    }

    private void setProgressBarVisibility(boolean visible) {
        if (isAdded()) {
            progressBarShows.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void addShowToFavorites(Show show, OnCompleteListener completeListener,
                            OnFailureListener failureListener) {
        new Util.Async.Task(() -> favoritesViewModel.addShow(show))
            .addOnCompleteListener(completeListener)
            .addOnFailureListener(failureListener)
            .execute();
    }

    private void removeShowFromFavorites(Show show, OnCompleteListener completeListener,
                                         OnFailureListener failureListener) {
       new Util.Async.Task(() -> favoritesViewModel.deleteShowById(show.getAddic7edId()))
            .addOnCompleteListener(completeListener)
            .addOnFailureListener(failureListener)
            .execute();
    }

    void checkIfShowIsOnFavorites(Context context, Show show,
                                         FindShowOnFavorites.ResultListener listener) {
        new FindShowOnFavorites(context, show.getAddic7edId(), listener).execute();
    }

    static class FindShowOnFavorites extends AsyncTask<Void, Void, Boolean> {
        private interface ResultListener {
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
