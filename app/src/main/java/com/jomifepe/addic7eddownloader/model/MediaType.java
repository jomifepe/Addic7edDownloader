package com.jomifepe.addic7eddownloader.model;

public enum MediaType {
    MOVIE, TV_SHOW;

//    private int value;

//    MediaType(int value) {
//        this.value = value;
//    }

    public static MediaType valueOf(int val) {
        return values()[val];
    }
}
