package com.race.arena.algorithm.sort;

import com.race.arena.algorithm.AbstractSortAlgorithm;
import com.race.arena.algorithm.TraceRecorder;
import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SortRunResult;
import org.springframework.stereotype.Component;

@Component
public class CountingSort extends AbstractSortAlgorithm {

    /** Safety valve so a demo click on "few-unique-large-range" can't OOM the JVM. */
    public static final long MAX_RANGE = 20_000_000L;

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "counting", "Counting Sort", "non-comparison",
            "O(n + k)", "O(n + k)", "O(n + k)", "O(n + k)",
            true, false, false,
            "Three linear passes over n plus one linear pass over the key range k",
            "Escapes the Omega(n log n) comparison-sort lower bound entirely because it never compares " +
                    "two elements -- it counts occurrences per key and uses prefix sums as placement addresses. " +
                    "That only pays off when k = O(n); when k >> n it degrades toward O(k), which is the whole " +
                    "point of the key-range slider on the breaking-the-bound page."
    );

    @Override
    public String id() {
        return "counting";
    }

    @Override
    public String displayName() {
        return "Counting Sort";
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

        if (a.length == 0) {
            return finish(a, rec, System.nanoTime() - start, true, false);
        }

        int min = a[0], max = a[0];
        for (int v : a) {
            min = Math.min(min, v);
            max = Math.max(max, v);
        }
        rec.addArrayAccess(2L * a.length);

        long range = (long) max - min + 1;
        if (range > MAX_RANGE) {
            throw new IllegalArgumentException(
                    "Counting sort key range too large (k=" + range + "); max supported k=" + MAX_RANGE +
                            ". This is exactly the O(n+k) weakness the demo illustrates.");
        }

        int[] count = new int[(int) range];
        rec.addExtraMemory(range + a.length);

        for (int v : a) {
            count[v - min]++;
        }
        rec.addArrayAccess(a.length);

        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        int[] output = new int[a.length];
        int[] shadowOutput = shadow != null ? new int[shadow.length] : null;
        for (int i = a.length - 1; i >= 0; i--) {
            int idx = --count[a[i] - min];
            output[idx] = a[i];
            if (shadow != null) shadowOutput[idx] = shadow[i];
            rec.addArrayAccess(2);
        }

        for (int i = 0; i < a.length; i++) {
            rec.overwrite(a, i, output[i], shadowOutput != null ? shadowOutput[i] : -1);
        }
        for (int i = 0; i < a.length; i++) rec.markSorted(i);

        long elapsed = System.nanoTime() - start;
        return finish(a, rec, elapsed, true, false);
    }
}
