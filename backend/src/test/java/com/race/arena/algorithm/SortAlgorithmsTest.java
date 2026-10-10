package com.race.arena.algorithm;

import com.race.arena.algorithm.sort.ArraysSortBaseline;
import com.race.arena.algorithm.sort.BubbleSort;
import com.race.arena.algorithm.sort.CountingSort;
import com.race.arena.algorithm.sort.HeapSort;
import com.race.arena.algorithm.sort.InsertionSort;
import com.race.arena.algorithm.sort.MergeSort;
import com.race.arena.algorithm.sort.QuickSortFirstPivot;
import com.race.arena.algorithm.sort.QuickSortRandomPivot;
import com.race.arena.algorithm.sort.RadixSort;
import com.race.arena.model.SortRunResult;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortAlgorithmsTest {

    static Stream<SortAlgorithm> allComparisonBasedAlgorithms() {
        // Counting and radix are tested separately (NegativeSupportTest) since they need
        // a bounded key range; the rest tolerate arbitrary ints including negatives.
        return Stream.of(
                new BubbleSort(), new InsertionSort(), new MergeSort(),
                new QuickSortRandomPivot(), new QuickSortFirstPivot(), new HeapSort(),
                new ArraysSortBaseline()
        );
    }

    static Stream<SortAlgorithm> allNonNegativeFriendlyAlgorithms() {
        return Stream.of(
                new BubbleSort(), new InsertionSort(), new MergeSort(),
                new QuickSortRandomPivot(), new QuickSortFirstPivot(), new HeapSort(),
                new CountingSort(), new RadixSort(), new ArraysSortBaseline()
        );
    }

    @ParameterizedTest
    @MethodSource("allNonNegativeFriendlyAlgorithms")
    void sortsEmptyArray(SortAlgorithm algo) {
        SortRunResult result = algo.runWithTrace(new int[]{});
        assertArrayEquals(new int[]{}, result.sortedArray());
    }

    @ParameterizedTest
    @MethodSource("allNonNegativeFriendlyAlgorithms")
    void sortsSingleElement(SortAlgorithm algo) {
        SortRunResult result = algo.runWithTrace(new int[]{42});
        assertArrayEquals(new int[]{42}, result.sortedArray());
    }

    @ParameterizedTest
    @MethodSource("allNonNegativeFriendlyAlgorithms")
    void sortsRandomInput(SortAlgorithm algo) {
        int[] input = {5, 3, 8, 1, 9, 2, 7, 0, 6, 4};
        int[] expected = input.clone();
        Arrays.sort(expected);
        SortRunResult result = algo.runWithTrace(input);
        assertArrayEquals(expected, result.sortedArray());
    }

    @ParameterizedTest
    @MethodSource("allNonNegativeFriendlyAlgorithms")
    void sortsAlreadySortedInput(SortAlgorithm algo) {
        int[] input = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        SortRunResult result = algo.runWithTrace(input.clone());
        assertArrayEquals(input, result.sortedArray());
    }

    @ParameterizedTest
    @MethodSource("allNonNegativeFriendlyAlgorithms")
    void sortsReversedInput(SortAlgorithm algo) {
        int[] input = {9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        int[] expected = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        SortRunResult result = algo.runWithTrace(input);
        assertArrayEquals(expected, result.sortedArray());
    }

    @ParameterizedTest
    @MethodSource("allNonNegativeFriendlyAlgorithms")
    void sortsManyDuplicates(SortAlgorithm algo) {
        int[] input = {3, 3, 1, 3, 2, 1, 2, 3, 1, 2};
        int[] expected = input.clone();
        Arrays.sort(expected);
        SortRunResult result = algo.runWithTrace(input);
        assertArrayEquals(expected, result.sortedArray());
    }

    @ParameterizedTest
    @MethodSource("allNonNegativeFriendlyAlgorithms")
    void sortsAllEqualElements(SortAlgorithm algo) {
        int[] input = {7, 7, 7, 7, 7};
        SortRunResult result = algo.runWithTrace(input.clone());
        assertArrayEquals(input, result.sortedArray());
    }

    @ParameterizedTest
    @MethodSource("allComparisonBasedAlgorithms")
    void sortsNegativeNumbers(SortAlgorithm algo) {
        int[] input = {-5, 3, -8, 1, 0, -2, 7, -1};
        int[] expected = input.clone();
        Arrays.sort(expected);
        SortRunResult result = algo.runWithTrace(input);
        assertArrayEquals(expected, result.sortedArray());
    }

    @ParameterizedTest
    @MethodSource("allNonNegativeFriendlyAlgorithms")
    void runFastProducesSameSortedResultAsTrace(SortAlgorithm algo) {
        int[] input = {5, 3, 8, 1, 9, 2, 7, 0, 6, 4};
        SortRunResult traced = algo.runWithTrace(input.clone());
        SortRunResult fast = algo.runFast(input.clone());
        assertArrayEquals(traced.sortedArray(), fast.sortedArray());
        assertTrue(fast.steps().isEmpty(), "runFast must not record a step trace");
    }

    @ParameterizedTest
    @MethodSource("allNonNegativeFriendlyAlgorithms")
    void metaFlagsAreInternallyConsistent(SortAlgorithm algo) {
        assertTrue(!algo.meta().id().isBlank());
        assertTrue(!algo.meta().name().isBlank());
    }
}
