package com.race.arena.algorithm.search;

import com.race.arena.algorithm.SearchAlgorithm;
import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SearchRunResult;
import com.race.arena.model.Step;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LinearSearch implements SearchAlgorithm {

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "linear-search", "Linear Search", "comparison",
            "O(1)", "O(n)", "O(n)", "O(1)",
            true, true, true,
            "n/a",
            "Scans every element until a match; works on unsorted data, but gives up binary search's " +
                    "O(log n) advantage entirely."
    );

    @Override
    public String id() {
        return "linear-search";
    }

    @Override
    public String displayName() {
        return "Linear Search";
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
        int foundIndex = -1;
        for (int i = 0; i < array.length; i++) {
            comparisons++;
            steps.add(Step.compareValue(i, target));
            if (array[i] == target) {
                foundIndex = i;
                steps.add(Step.found(i));
                break;
            }
        }
        if (foundIndex == -1) steps.add(Step.notFound());
        long elapsed = System.nanoTime() - start;
        return new SearchRunResult(foundIndex, steps, comparisons, elapsed);
    }
}
