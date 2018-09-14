package com.jomifepe.addic7eddownloader.model.comparator;

import com.jomifepe.addic7eddownloader.model.TVShow;

import java.util.Comparator;

public class MediaComparator implements Comparator<TVShow> {
    @Override
    public int compare(TVShow s1, TVShow s2) {
        return s1.getTitle().compareTo(s2.getTitle());
    }
}
