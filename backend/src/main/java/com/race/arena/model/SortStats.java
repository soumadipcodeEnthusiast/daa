package com.race.arena.model;

public record SortStats(
        long comparisons,
        long swaps,
        long writes,
        long arrayAccesses,
        long elapsedNanos,
        long extraMemoryUnits,
        boolean stable,
        boolean inPlace
) {
}
