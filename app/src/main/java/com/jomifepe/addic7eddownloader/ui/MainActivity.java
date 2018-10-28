package com.jomifepe.addic7eddownloader.ui;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.util.ErrorUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    TabAdapter tabAdapter;
    SearchView searchView;

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.navigation) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ErrorUtil.DefaultExceptionHandler(this);

        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        tabAdapter = new TabAdapter(getSupportFragmentManager(), this)
                .addTab(new TVShowsFragment(), NavTab.TVSHOWS)
                .addTab(new SearchFragment(), NavTab.SEARCH)
                .addTab(new FavoritesFragment(), NavTab.FAVORITES);

        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.setCurrentItem(NavTab.TVSHOWS.getValue());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_tvshows:
                        changeTab(NavTab.TVSHOWS);
                        return true;
                    case R.id.navigation_search:
                        changeTab(NavTab.SEARCH);
                        return true;
                    case R.id.navigation_favorites:
                        changeTab(NavTab.FAVORITES);
                        return true;
                }
                return false;
            };

    public void changeTab(NavTab tab) {
        viewPager.setCurrentItem(tab.getValue(), true);
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            bottomNavigationView.getMenu().getItem(position).setChecked(true);
        }

        @Override
        public void onPageSelected(int position) {
            MainActivity.this.setTitle(tabAdapter.getPageTitle(position));
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_tvshows, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search_tvshows_icon);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }
}
