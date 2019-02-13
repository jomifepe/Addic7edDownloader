package com.jomifepe.addic7eddownloader.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.ui.listener.FragmentLoadListener;

public class SearchFragment extends BaseFragment {

    public SearchFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view =  inflater.inflate(R.layout.fragment_search, container, false);

        onFragmentLoad();
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.mi_main_shows_filter);
        menuItem.setVisible(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
