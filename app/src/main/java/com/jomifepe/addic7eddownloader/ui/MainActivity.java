package com.jomifepe.addic7eddownloader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.ui.listener.FragmentLoadListener;
import com.jomifepe.addic7eddownloader.util.Util;

import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity
        extends BaseActivity
        implements FragmentLoadListener{

    @BindView(R.id.layout_main) FrameLayout fragmentContainer;
    @BindView(R.id.bnv_main_navigation) BottomNavigationView bottomNavigationView;

    private FragmentManager fragmentManager;
    private MenuItem menuSearchItem;
    private SearchView menuSearchView;
    private boolean isTransitioning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bottomNavigationView.setOnNavigationItemSelectedListener(navItemClickListener);

        String defaultSectionName = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.key_pref_general_default_screen), Section.SHOWS.name());
        Section defaultSection = Section.valueOf(defaultSectionName, true);

        if (fragmentContainer != null) {
            if (savedInstanceState != null) return;

            try {
                fragmentManager = getSupportFragmentManager();
                setDefaultSection(defaultSection);
            } catch (Exception e) {
                showMessage(R.string.error_generic);
            }
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuSearchItem = menu.findItem(R.id.mi_main_shows_filter);
        menuSearchView = (SearchView) menuSearchItem.getActionView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mi_main_preferences:
                Intent settingsIntent = new Intent(this, PreferencesActivity.class);
                startActivity(settingsIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Fragment lastFragment = getLastFragment();
            Section section = Section.valueOf(lastFragment.getClass());
            selectNavigation(section);
            setTitle(section.getTitle(this));
        }
    }

    @Override
    public void onFragmentLoad() {
        this.isTransitioning = false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navItemClickListener = item -> {
        if (!isTransitioning) {
            switch (item.getItemId()) {
                case R.id.navigation_tvshows:
                    changeSection(Section.SHOWS, true);
                    return true;
                case R.id.navigation_search:
                    changeSection(Section.SEARCH, true);
                    return true;
                case R.id.navigation_favorites:
                    changeSection(Section.FAVORITES, true);
                    return true;
            }
        }
        return false;
    };

    private void setDefaultSection(Section section) {
        Fragment fragment = section.instantiate();
        fragmentManager.beginTransaction()
                .add(R.id.layout_main, fragment, fragment.getClass().getName())
                .commit();
        selectNavigation(section);
        setTitle(section.getTitle(this));
    }

    private void changeSection(Section section, boolean animated) {
        if (!isCurrentSection(section)) {
            BaseFragment fragment = section.instantiate();
            transitionToFragment(fragment, animated);
            selectNavigation(section);
            setTitle(section.getTitle(this));
        }
    }

    private <F extends BaseFragment> void transitionToFragment(F fragment, boolean animated) {
        isTransitioning = true;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (animated) {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        }
        fragmentTransaction.replace(R.id.layout_main, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    private void selectNavigation(Section section) {
        bottomNavigationView.getMenu().getItem(section.ordinal()).setChecked(true);
    }

    private boolean isCurrentSection(Section section) {
        Fragment fragment = fragmentManager.findFragmentByTag(section.getFragmentClass().getName());
        return fragment != null && fragment.isVisible();
    }

    private Fragment getLastFragment() {
        FragmentManager.BackStackEntry lastBackStackEntry = fragmentManager
                .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
        return fragmentManager.findFragmentByTag(lastBackStackEntry.getName());
    }
}
