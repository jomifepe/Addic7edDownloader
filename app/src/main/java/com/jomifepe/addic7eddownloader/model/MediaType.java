package com.jomifepe.addic7eddownloader.model;

import android.content.Context;

import com.jomifepe.addic7eddownloader.R;

import androidx.annotation.StringRes;

public enum MediaType {
    MOVIE(R.string.label_movie),
    SHOW(R.string.label_show);

    private @StringRes int label;

    MediaType(int label) {
        this.label = label;
    }

    public String getTitle(Context context) {
        return context.getString(label);
    }

    public static MediaType valueOf(int val) {
        return values()[val];
    }
}
