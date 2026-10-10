package com.race.arena.algorithm.sort;

import com.race.arena.algorithm.AbstractSortAlgorithm;
import com.race.arena.algorithm.TraceRecorder;
import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SortRunResult;
import org.springframework.stereotype.Component;

/** LSD radix sort, base 10. Negatives are handled by shifting the whole array to be non-negative first. */
@Component
public class RadixSort extends AbstractSortAlgorithm {

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "radix", "Radix Sort (LSD, base 10)", "non-comparison",
            "O(d*(n+b))", "O(d*(n+b))", "O(d*(n+b))", "O(n + b)",
            true, false, false,
            "d passes of counting sort by digit, each O(n + b) with base b=10",
            "With d = number of digits in the largest key and b = 10, total work is O(d*(n+b)). " +
                    "Since d = O(log_b(maxValue)) is a small constant for fixed-width integers, this is " +
                    "effectively linear in n -- another comparison-free escape from the Omega(n log n) bound."
    );

    @Override
    public String id() {
        return "radix";
    }

    @Override
    public String displayName() {
        return "Radix Sort (LSD)";
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

        int min = a[0];
        for (int v : a) min = Math.min(min, v);

        int[] shifted = new int[a.length];
        for (int i = 0; i < a.length; i++) shifted[i] = a[i] - min;

        int max = 0;
        for (int v : shifted) max = Math.max(max, v);

        rec.addExtraMemory(a.length + 10);
        int[] output = new int[a.length];
        int[] shiftedShadow = shadow != null ? shadow.clone() : null;
        int[] shadowOutput = shadow != null ? new int[shadow.length] : null;

        for (long exp = 1; max / exp > 0; exp *= 10) {
            int[] count = new int[10];
            for (int v : shifted) {
                count[(int) ((v / exp) % 10)]++;
            }
            rec.addArrayAccess(a.length);
            for (int i = 1; i < 10; i++) count[i] += count[i - 1];

            for (int i = a.length - 1; i >= 0; i--) {
                int digit = (int) ((shifted[i] / exp) % 10);
                int idx = --count[digit];
                output[idx] = shifted[i];
                if (shiftedShadow != null) shadowOutput[idx] = shiftedShadow[i];
                rec.addArrayAccess(2);
            }
            System.arraycopy(output, 0, shifted, 0, a.length);
            if (shiftedShadow != null) System.arraycopy(shadowOutput, 0, shiftedShadow, 0, a.length);
        }

        for (int i = 0; i < a.length; i++) {
            rec.overwrite(a, i, shifted[i] + min, shiftedShadow != null ? shiftedShadow[i] : -1);
        }
        for (int i = 0; i < a.length; i++) rec.markSorted(i);

        long elapsed = System.nanoTime() - start;
        return finish(a, rec, elapsed, true, false);
    }
}
