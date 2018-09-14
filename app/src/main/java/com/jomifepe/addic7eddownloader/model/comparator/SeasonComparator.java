package com.jomifepe.addic7eddownloader.model.comparator;

import com.jomifepe.addic7eddownloader.model.Season;

import java.util.Comparator;

public class SeasonComparator implements Comparator<Season> {
    @Override
    public int compare(Season s1, Season s2) {
        return s1.getNumber().compareTo(s2.getNumber());
    }
}
