package com.race.arena.algorithm.sort;

import com.race.arena.algorithm.AbstractSortAlgorithm;
import com.race.arena.algorithm.TraceRecorder;
import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SortRunResult;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/** Benchmark-only reference point: the JDK's dual-pivot quicksort / Timsort hybrid. No step trace. */
@Component
public class ArraysSortBaseline extends AbstractSortAlgorithm {

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "baseline", "java.util.Arrays.sort (baseline)", "comparison",
            "O(n)", "O(n log n)", "O(n log n)", "O(log n)",
            true, false, true,
            "n/a (library implementation: dual-pivot quicksort for primitives)",
            "Benchmark-only reference line: shows how much headroom a highly-tuned, JIT-friendly " +
                    "production sort has over the hand-written algorithms on this page."
    );

    @Override
    public String id() {
        return "baseline";
    }

    @Override
    public String displayName() {
        return "Arrays.sort (baseline)";
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
        Arrays.sort(a);
        long elapsed = System.nanoTime() - start;
        for (int i = 0; i < a.length; i++) rec.markSorted(i);
        return finish(a, rec, elapsed, true, false);
    }
}
