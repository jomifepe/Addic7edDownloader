package com.jomifepe.addic7eddownloader.model;

public enum MediaType {
    MOVIE(0), TV_SHOW(1);

    private int value;

    MediaType(int value) {
        this.value = value;
    }

    public static MediaType valueOf(int val) {
        return values()[val];
    }
}
