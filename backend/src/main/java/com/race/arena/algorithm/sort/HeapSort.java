package com.race.arena.algorithm.sort;

import com.race.arena.algorithm.AbstractSortAlgorithm;
import com.race.arena.algorithm.TraceRecorder;
import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SortRunResult;
import org.springframework.stereotype.Component;

@Component
public class HeapSort extends AbstractSortAlgorithm {

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "heap", "Heap Sort", "comparison",
            "O(n log n)", "O(n log n)", "O(n log n)", "O(1)",
            false, true, true,
            "Build-heap: sum_{h=0}^{log n} n/2^h * O(h) = O(n); then n extractions at O(log n) each",
            "build-heap looks like O(n log n) at first glance but is actually O(n): most nodes are near " +
                    "the bottom and sift down only a short distance, and the geometric series sum_h h/2^h converges " +
                    "to a constant. The O(n log n) total comes entirely from the n extract-max calls."
    );

    @Override
    public String id() {
        return "heap";
    }

    @Override
    public String displayName() {
        return "Heap Sort";
    }

    @Override
    public AlgorithmMeta meta() {
        return META;
    }

    @Override
    public SortRunResult runWithTrace(int[] input) {
        return run(input, true);
    }

    @Override
    public SortRunResult runFast(int[] input) {
        return run(input, false);
    }

    private SortRunResult run(int[] input, boolean trace) {
        int[] a = input.clone();
        TraceRecorder rec = new TraceRecorder(trace);
        long start = System.nanoTime();
        int n = a.length;

        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(a, i, n, rec);
        }
        for (int end = n - 1; end > 0; end--) {
            rec.swap(a, 0, end);
            rec.markSorted(end);
            siftDown(a, 0, end, rec);
        }
        if (n > 0) rec.markSorted(0);

        long elapsed = System.nanoTime() - start;
        return finish(a, rec, elapsed, false, true);
    }

    private void siftDown(int[] a, int i, int size, TraceRecorder rec) {
        while (true) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < size && rec.compare(a, left, largest) > 0) largest = left;
            if (right < size && rec.compare(a, right, largest) > 0) largest = right;
            if (largest == i) break;
            rec.swap(a, i, largest);
            i = largest;
        }
    }
}
