package com.race.arena.algorithm.sort;

import com.race.arena.algorithm.AbstractSortAlgorithm;
import com.race.arena.algorithm.TraceRecorder;
import com.race.arena.model.SortRunResult;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Shared Lomuto-partition quicksort. Uses an explicit stack instead of recursion so that the
 * first-element-pivot variant doesn't blow the call stack when it hits its O(n^2) worst case
 * on large sorted/reversed inputs (which would otherwise recurse to a depth of n).
 */
abstract class QuickSortBase extends AbstractSortAlgorithm {

    protected abstract boolean usesRandomPivot();

    protected SortRunResult run(int[] input, boolean trace) {
        int[] a = input.clone();
        TraceRecorder rec = new TraceRecorder(trace);
        long start = System.nanoTime();

        Deque<int[]> stack = new ArrayDeque<>();
        if (a.length > 1) stack.push(new int[]{0, a.length - 1});

        while (!stack.isEmpty()) {
            int[] range = stack.pop();
            int lo = range[0], hi = range[1];
            if (lo >= hi) continue;
            int p = partition(a, lo, hi, rec);
            stack.push(new int[]{lo, p - 1});
            stack.push(new int[]{p + 1, hi});
        }

        for (int i = 0; i < a.length; i++) rec.markSorted(i);
        long elapsed = System.nanoTime() - start;
        return finish(a, rec, elapsed, false, true);
    }

    private int partition(int[] a, int lo, int hi, TraceRecorder rec) {
        int pivotIndex = usesRandomPivot() ? lo + ThreadLocalRandom.current().nextInt(hi - lo + 1) : lo;
        if (pivotIndex != hi) {
            rec.swap(a, pivotIndex, hi);
        }
        int pivotValue = a[hi];
        rec.pivot(hi);

        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (rec.compareValue(a, j, pivotValue) <= 0) {
                i++;
                if (i != j) rec.swap(a, i, j);
            }
        }
        rec.swap(a, i + 1, hi);
        return i + 1;
    }
}
