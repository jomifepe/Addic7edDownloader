package com.jomifepe.addic7eddownloader.ui;

import com.jomifepe.addic7eddownloader.R;

public enum NavTab {
    TVSHOWS(R.string.title_fragment_tvshows, R.layout.fragment_tvshows),
    SEARCH(R.string.title_fragment_search, R.layout.fragment_search),
    FAVORITES(R.string.title_fragment_favorites, R.layout.fragment_favorites),;

    private int titleResId;
    private int layoutResId;

    NavTab(int titleResId, int layoutResId) {
        this.titleResId = titleResId;
        this.layoutResId = layoutResId;
    }

    public int getValue() {
        return ordinal();
    }

    public static NavTab valueOf(int index) {
        if (index > values().length) {
            return null;
        }
        return values()[index];
    }

    public int getTitleResId() {
        return titleResId;
    }

    public int getLayoutResId() {
        return layoutResId;
    }
}
