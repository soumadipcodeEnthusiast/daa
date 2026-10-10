package com.race.arena.algorithm;

import com.race.arena.model.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Every sort algorithm routes its comparisons/swaps/writes through this class.
 * When `recording` is false (used for large-n benchmark runs) step objects are
 * never allocated, so instrumentation overhead stays to a handful of counters.
 *
 * A "shadow" array, when attached, receives every swap/overwrite applied to the
 * main array at the same indices. Tests use this to verify stability: the shadow
 * starts as the original indices [0..n-1], and after a stable sort, elements with
 * equal keys must keep their shadow values in increasing order.
 */
public final class TraceRecorder {

    private final boolean recording;
    private final List<Step> steps;
    private long comparisons = 0;
    private long swaps = 0;
    private long writes = 0;
    private long arrayAccesses = 0;
    private long extraMemoryUnits = 0;
    private int[] shadow;

    public TraceRecorder(boolean recording) {
        this.recording = recording;
        this.steps = recording ? new ArrayList<>() : List.of();
    }

    public void attachShadow(int[] shadow) {
        this.shadow = shadow;
    }

    public int compare(int[] a, int i, int j) {
        comparisons++;
        arrayAccesses += 2;
        if (recording) steps.add(Step.compare(i, j));
        return Integer.compare(a[i], a[j]);
    }

    public int compareValue(int[] a, int i, int value) {
        comparisons++;
        arrayAccesses += 1;
        if (recording) steps.add(Step.compareValue(i, value));
        return Integer.compare(a[i], value);
    }

    /** Logs a comparison between two logical positions without touching the main array (used by merge). */
    public void logCompare(int i, int j) {
        comparisons++;
        arrayAccesses += 2;
        if (recording) steps.add(Step.compare(i, j));
    }

    public void swap(int[] a, int i, int j) {
        swaps++;
        arrayAccesses += 4;
        if (recording) steps.add(Step.swap(i, j));
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
        if (shadow != null) {
            int t = shadow[i];
            shadow[i] = shadow[j];
            shadow[j] = t;
        }
    }

    public void overwrite(int[] a, int i, int value) {
        overwrite(a, i, value, -1);
    }

    /** Overwrite a[i] with value; if a shadow array is attached, also sets shadow[i] = shadowValue. */
    public void overwrite(int[] a, int i, int value, int shadowValue) {
        writes++;
        arrayAccesses += 1;
        if (recording) steps.add(Step.overwrite(i, value));
        a[i] = value;
        if (shadow != null && shadowValue != -1) {
            shadow[i] = shadowValue;
        }
    }

    public void markSorted(int i) {
        if (recording) steps.add(Step.markSorted(i));
    }

    public void pivot(int i) {
        if (recording) steps.add(Step.pivot(i));
    }

    public void range(int lo, int hi, int mid) {
        if (recording) steps.add(Step.range(lo, hi, mid));
    }

    public void addExtraMemory(long units) {
        extraMemoryUnits += units;
    }

    public void addArrayAccess(long count) {
        arrayAccesses += count;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public long getWrites() {
        return writes;
    }

    public long getArrayAccesses() {
        return arrayAccesses;
    }

    public long getExtraMemoryUnits() {
        return extraMemoryUnits;
    }
}
