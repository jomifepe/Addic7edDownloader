package com.jomifepe.addic7eddownloader.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {
    Context context;
    private final List<Tab> tabList = new ArrayList<>();

    public static final class Tab {
        private final Fragment fragment;
        private final @StringRes
        int title;
        private final int id;

        Tab(Fragment fragment, @StringRes int title, int id) {
            this.fragment = fragment;
            this.title = title;
            this.id = id;
        }
    }

    public TabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public TabAdapter addTab(Fragment fragment, NavTab tab) {
        tabList.add(new Tab(fragment, tab.getTitleResId(), tab.ordinal()));
        return this;
    }

    @Override
    public Fragment getItem(int position) {
        return tabList.get(position).fragment;
    }

    @Override
    public int getCount() {
        return tabList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(tabList.get(position).title);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
