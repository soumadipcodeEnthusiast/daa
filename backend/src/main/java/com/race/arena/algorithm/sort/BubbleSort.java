package com.race.arena.algorithm.sort;

import com.race.arena.algorithm.AbstractSortAlgorithm;
import com.race.arena.algorithm.TraceRecorder;
import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SortRunResult;
import org.springframework.stereotype.Component;

@Component
public class BubbleSort extends AbstractSortAlgorithm {

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "bubble", "Bubble Sort", "comparison",
            "O(n)", "O(n^2)", "O(n^2)", "O(1)",
            true, true, true,
            "T(n) = T(n-1) + O(n), with early exit if a pass makes zero swaps",
            "Repeatedly bubbles the largest unsorted element to its final position; the early-exit flag " +
                    "makes an already-sorted array the best case at O(n)."
    );

    @Override
    public String id() {
        return "bubble";
    }

    @Override
    public String displayName() {
        return "Bubble Sort";
    }

    @Override
    public AlgorithmMeta meta() {
        return META;
    }

    @Override
    public SortRunResult runWithTrace(int[] input) {
        return run(input, true, null);
    }

    @Override
    public SortRunResult runFast(int[] input) {
        return run(input, false, null);
    }

    /** Test hook: runs with a shadow array mirroring every swap, to verify stability. */
    public SortRunResult runWithShadow(int[] input, int[] shadow) {
        return run(input, true, shadow);
    }

    private SortRunResult run(int[] input, boolean trace, int[] shadow) {
        int[] a = input.clone();
        TraceRecorder rec = new TraceRecorder(trace);
        if (shadow != null) rec.attachShadow(shadow);
        long start = System.nanoTime();
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (rec.compare(a, j, j + 1) > 0) {
                    rec.swap(a, j, j + 1);
                    swapped = true;
                }
            }
            rec.markSorted(n - 1 - i);
            if (!swapped) break;
        }
        if (n > 0) rec.markSorted(0);
        long elapsed = System.nanoTime() - start;
        return finish(a, rec, elapsed, true, true);
    }
}
