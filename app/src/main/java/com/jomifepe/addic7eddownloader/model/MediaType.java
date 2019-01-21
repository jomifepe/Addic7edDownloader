package com.jomifepe.addic7eddownloader.model;

public enum MediaType {
    MOVIE, TV_SHOW;

    public static MediaType valueOf(int val) {
        return values()[val];
    }
}
