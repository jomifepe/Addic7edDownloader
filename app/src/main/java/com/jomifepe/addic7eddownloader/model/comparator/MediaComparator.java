package com.jomifepe.addic7eddownloader.model.comparator;

import com.jomifepe.addic7eddownloader.model.Show;

import java.util.Comparator;

public class MediaComparator implements Comparator<Show> {
    @Override
    public int compare(Show s1, Show s2) {
        return s1.getTitle().compareTo(s2.getTitle());
    }
}
