package com.race.arena.algorithm;

import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SearchRunResult;

public interface SearchAlgorithm {

    String id();

    String displayName();

    AlgorithmMeta meta();

    /** Input must already be sorted ascending for binary search; linear search tolerates any order. */
    SearchRunResult search(int[] sortedArray, int target);
}
