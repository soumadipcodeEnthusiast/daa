package com.race.arena.algorithm;

import com.race.arena.model.SortRunResult;
import com.race.arena.model.SortStats;

public abstract class AbstractSortAlgorithm implements SortAlgorithm {

    protected SortRunResult finish(int[] sortedArray, TraceRecorder rec, long elapsedNanos, boolean stable, boolean inPlace) {
        SortStats stats = new SortStats(
                rec.getComparisons(),
                rec.getSwaps(),
                rec.getWrites(),
                rec.getArrayAccesses(),
                elapsedNanos,
                rec.getExtraMemoryUnits(),
                stable,
                inPlace
        );
        return new SortRunResult(sortedArray, rec.getSteps(), stats);
    }
}
