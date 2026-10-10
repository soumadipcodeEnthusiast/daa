package com.race.arena.service;

import com.race.arena.algorithm.SearchAlgorithm;
import com.race.arena.algorithm.SortAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlgorithmRegistry {

    private final Map<String, SortAlgorithm> sortAlgorithms = new LinkedHashMap<>();
    private final Map<String, SearchAlgorithm> searchAlgorithms = new LinkedHashMap<>();

    public AlgorithmRegistry(List<SortAlgorithm> sorts, List<SearchAlgorithm> searches) {
        for (SortAlgorithm s : sorts) sortAlgorithms.put(s.id(), s);
        for (SearchAlgorithm s : searches) searchAlgorithms.put(s.id(), s);
    }

    public SortAlgorithm sort(String id) {
        SortAlgorithm algo = sortAlgorithms.get(id);
        if (algo == null) {
            throw new IllegalArgumentException("Unknown sort algorithm: " + id);
        }
        return algo;
    }

    public SearchAlgorithm search(String id) {
        SearchAlgorithm algo = searchAlgorithms.get(id);
        if (algo == null) {
            throw new IllegalArgumentException("Unknown search algorithm: " + id);
        }
        return algo;
    }

    public Collection<SortAlgorithm> allSorts() {
        return sortAlgorithms.values();
    }

    public Collection<SearchAlgorithm> allSearches() {
        return searchAlgorithms.values();
    }

    /** Sorts whose worst case is quadratic -- benchmark auto-skips these above a safe size. */
    public boolean isQuadraticWorstCase(String id) {
        return id.equals("bubble") || id.equals("insertion") || id.equals("quick-first");
    }
}
