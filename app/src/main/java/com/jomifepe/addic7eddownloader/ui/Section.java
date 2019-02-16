package com.jomifepe.addic7eddownloader.ui;

import android.content.Context;
import androidx.annotation.ArrayRes;
import androidx.fragment.app.Fragment;

import com.jomifepe.addic7eddownloader.R;

public enum Section {
    SHOWS(ShowsFragment.class),
    SEARCH(SearchFragment.class),
    FAVORITES(FavoritesFragment.class);

    private static final @ArrayRes int RESOURCE_ARRAY = R.array.navigation_sections;
    private final Class fragmentClass;

    <F extends Fragment> Section(Class<F> fragmentClass) {
        this.fragmentClass = fragmentClass;
    }

    public static Section get(int index) {
        if (index > 0 && index < count()) {
            return values()[index];
        }
        return null;
    }

    public static Section valueOf(String name, boolean ignoreCase) {
        for (Section v : values()) {
            if (ignoreCase) {
                if (v.name().equalsIgnoreCase(name)) {
                    return v;
                }
            } else {
                if (v.name().equals(name)) {
                    return v;
                }
            }
        }
        throw new IllegalArgumentException();
    }

    public static <F extends Fragment> Section valueOf(Class<F> fragmentClass) {
        for (Section value : values()) {
            if (value.getFragmentClass().equals(fragmentClass)) {
                return value;
            }
        }
        throw new IllegalArgumentException();
    }

    public static int count() {
        return values().length;
    }

    public <F extends Fragment> F instantiate() {
        try {
            return (F) fragmentClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            return null;
        }
    }

    public Class getFragmentClass() {
        return fragmentClass;
    }

    public String getTitle(Context context) {
        return context.getResources().getStringArray(RESOURCE_ARRAY)[ordinal()];
    }
}
