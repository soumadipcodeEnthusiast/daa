package com.race.arena.service;

import com.race.arena.algorithm.SortAlgorithm;
import com.race.arena.model.BenchmarkRun;
import com.race.arena.model.SortRunResult;
import com.race.arena.repository.BenchmarkRunRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BenchmarkService {

    public static final int MAX_SIZE_QUADRATIC = 20_000;
    public static final int MAX_SIZE_FAST = 1_000_000;

    private final AlgorithmRegistry registry;
    private final InputGenerator inputGenerator;
    private final BenchmarkRunRepository repository;

    public BenchmarkService(AlgorithmRegistry registry, InputGenerator inputGenerator, BenchmarkRunRepository repository) {
        this.registry = registry;
        this.inputGenerator = inputGenerator;
        this.repository = repository;
    }

    public record BenchmarkPoint(int size, long medianNanos, long comparisons, boolean skipped, String skipReason) {
    }

    public record BenchmarkSeries(String algorithm, List<BenchmarkPoint> points) {
    }

    public List<BenchmarkSeries> run(List<String> algorithmIds, String inputType, List<Integer> sizes, int repetitions) {
        if (repetitions < 1) throw new IllegalArgumentException("repetitions must be >= 1");
        List<BenchmarkSeries> series = new ArrayList<>();

        for (String algoId : algorithmIds) {
            SortAlgorithm algo = registry.sort(algoId);
            List<BenchmarkPoint> points = new ArrayList<>();

            for (int size : sizes) {
                int cap = registry.isQuadraticWorstCase(algoId) ? MAX_SIZE_QUADRATIC : MAX_SIZE_FAST;
                if (size > cap) {
                    points.add(new BenchmarkPoint(size, 0, 0, true,
                            "Skipped: n=" + size + " exceeds the safe cap of " + cap + " for this algorithm's worst case"));
                    continue;
                }

                int[] input = inputGenerator.generate(inputType, size, 42L);

                // JIT warm-up: run once (and discard) before the timed repetitions.
                algo.runFast(input);

                long[] timings = new long[repetitions];
                long comparisons = 0;
                for (int r = 0; r < repetitions; r++) {
                    SortRunResult result = algo.runFast(input);
                    timings[r] = result.stats().elapsedNanos();
                    comparisons = result.stats().comparisons();
                }
                long median = median(timings);
                points.add(new BenchmarkPoint(size, median, comparisons, false, null));

                repository.save(new BenchmarkRun(algoId, inputType, size, repetitions, median, comparisons));
            }
            series.add(new BenchmarkSeries(algoId, points));
        }
        return series;
    }

    private long median(long[] values) {
        long[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        int n = sorted.length;
        if (n % 2 == 1) return sorted[n / 2];
        return (sorted[n / 2 - 1] + sorted[n / 2]) / 2;
    }
}
