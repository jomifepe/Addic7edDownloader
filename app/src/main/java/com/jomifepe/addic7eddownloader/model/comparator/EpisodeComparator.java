package com.jomifepe.addic7eddownloader.model.comparator;

import com.jomifepe.addic7eddownloader.model.Episode;

import java.util.Comparator;

public class EpisodeComparator implements Comparator<Episode> {
    @Override
    public int compare(Episode ep1, Episode ep2) {
        int titleComparison = ep1.getNumber().compareTo(ep2.getNumber());
        if (titleComparison == 0) {
            return ep1.getTitle().compareTo(ep2.getTitle());
        }
        return titleComparison;
    }
}
