package com.race.arena.algorithm.sort;

import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SortRunResult;
import org.springframework.stereotype.Component;

@Component
public class QuickSortFirstPivot extends QuickSortBase {

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "quick-first", "Quick Sort (first-element pivot)", "comparison",
            "O(n log n)", "O(n log n)", "O(n^2)", "O(log n)",
            false, true, true,
            "T(n) = T(0) + T(n-1) + O(n) on sorted/reversed input, which solves to O(n^2)",
            "Always picking a[lo] as pivot is an easy adversary: on an already-sorted (or reversed) " +
                    "array the pivot is always the extreme element, so partition splits it into sizes 0 and " +
                    "n-1 every single time -- the textbook O(n^2) degenerate case."
    );

    @Override
    protected boolean usesRandomPivot() {
        return false;
    }

    @Override
    public String id() {
        return "quick-first";
    }

    @Override
    public String displayName() {
        return "Quick Sort (first-element pivot)";
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
}
