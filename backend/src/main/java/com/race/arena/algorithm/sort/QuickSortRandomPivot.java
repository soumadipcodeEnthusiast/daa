package com.race.arena.algorithm.sort;

import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SortRunResult;
import org.springframework.stereotype.Component;

@Component
public class QuickSortRandomPivot extends QuickSortBase {

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "quick-random", "Quick Sort (random pivot)", "comparison",
            "O(n log n)", "O(n log n)", "O(n^2)", "O(log n)",
            false, true, true,
            "T(n) = T(k) + T(n-k-1) + O(n), expected O(n log n) via indicator random variables",
            "Randomizing the pivot makes the adversarial O(n^2) input vanish: for any fixed input, " +
                    "E[comparisons] ~ 1.39 n log2 n, derived by summing indicator variables X_ij over the " +
                    "probability that elements i and j are ever compared."
    );

    @Override
    protected boolean usesRandomPivot() {
        return true;
    }

    @Override
    public String id() {
        return "quick-random";
    }

    @Override
    public String displayName() {
        return "Quick Sort (random pivot)";
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
