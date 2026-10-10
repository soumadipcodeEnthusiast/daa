package com.race.arena.algorithm.sort;

import com.race.arena.algorithm.AbstractSortAlgorithm;
import com.race.arena.algorithm.TraceRecorder;
import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SortRunResult;
import org.springframework.stereotype.Component;

@Component
public class InsertionSort extends AbstractSortAlgorithm {

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "insertion", "Insertion Sort", "comparison",
            "O(n)", "O(n^2)", "O(n^2)", "O(1)",
            true, true, true,
            "T(n) = T(n-1) + O(n) worst case; O(n) best case on nearly-sorted input",
            "Builds the sorted prefix one element at a time, shifting larger elements right; " +
                    "very fast in practice on nearly-sorted data, which is why it anchors Timsort's insertion runs."
    );

    @Override
    public String id() {
        return "insertion";
    }

    @Override
    public String displayName() {
        return "Insertion Sort";
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

    public SortRunResult runWithShadow(int[] input, int[] shadow) {
        return run(input, true, shadow);
    }

    private SortRunResult run(int[] input, boolean trace, int[] shadow) {
        int[] a = input.clone();
        TraceRecorder rec = new TraceRecorder(trace);
        if (shadow != null) rec.attachShadow(shadow);
        long start = System.nanoTime();
        int n = a.length;
        for (int i = 1; i < n; i++) {
            int j = i;
            while (j > 0 && rec.compare(a, j - 1, j) > 0) {
                rec.swap(a, j - 1, j);
                j--;
            }
            rec.markSorted(i);
        }
        if (n > 0) rec.markSorted(0);
        long elapsed = System.nanoTime() - start;
        return finish(a, rec, elapsed, true, true);
    }
}
