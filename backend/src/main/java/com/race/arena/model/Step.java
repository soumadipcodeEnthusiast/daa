package com.race.arena.model;

/**
 * One animation-ready event emitted while an algorithm runs.
 * type is one of: compare, swap, overwrite, mark-sorted, pivot, range, found, not-found.
 * j and value are -1 / unused when not applicable to that type.
 */
public record Step(String type, int i, int j, int value) {

    public static Step compare(int i, int j) {
        return new Step("compare", i, j, 0);
    }

    public static Step compareValue(int i, int value) {
        return new Step("compare", i, -1, value);
    }

    public static Step swap(int i, int j) {
        return new Step("swap", i, j, 0);
    }

    public static Step overwrite(int i, int value) {
        return new Step("overwrite", i, -1, value);
    }

    public static Step markSorted(int i) {
        return new Step("mark-sorted", i, -1, 0);
    }

    public static Step pivot(int i) {
        return new Step("pivot", i, -1, 0);
    }

    public static Step range(int lo, int hi, int mid) {
        return new Step("range", lo, hi, mid);
    }

    public static Step found(int i) {
        return new Step("found", i, -1, 0);
    }

    public static Step notFound() {
        return new Step("not-found", -1, -1, 0);
    }
}
