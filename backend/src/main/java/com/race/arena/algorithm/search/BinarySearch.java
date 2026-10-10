package com.race.arena.algorithm.search;

import com.race.arena.algorithm.SearchAlgorithm;
import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SearchRunResult;
import com.race.arena.model.Step;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BinarySearch implements SearchAlgorithm {

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "binary-search", "Binary Search (iterative)", "comparison",
            "O(1)", "O(log n)", "O(log n)", "O(1)",
            true, true, true,
            "T(n) = T(n/2) + O(1)",
            "Halves the live range [lo, hi] every comparison, so it matches the information-theoretic " +
                    "floor of log2(n) comparisons for locating one of n positions -- but only works on sorted data."
    );

    @Override
    public String id() {
        return "binary-search";
    }

    @Override
    public String displayName() {
        return "Binary Search";
    }

    @Override
    public AlgorithmMeta meta() {
        return META;
    }

    @Override
    public SearchRunResult search(int[] array, int target) {
        List<Step> steps = new ArrayList<>();
        long start = System.nanoTime();
        long comparisons = 0;
        int lo = 0, hi = array.length - 1;
        int foundIndex = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            steps.add(Step.range(lo, hi, mid));
            comparisons++;
            steps.add(Step.compareValue(mid, target));
            if (array[mid] == target) {
                foundIndex = mid;
                steps.add(Step.found(mid));
                break;
            } else if (array[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        if (foundIndex == -1) steps.add(Step.notFound());
        long elapsed = System.nanoTime() - start;
        return new SearchRunResult(foundIndex, steps, comparisons, elapsed);
    }
}
