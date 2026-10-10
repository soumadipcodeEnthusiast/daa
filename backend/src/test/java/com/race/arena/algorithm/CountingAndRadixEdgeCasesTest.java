package com.race.arena.algorithm;

import com.race.arena.algorithm.sort.CountingSort;
import com.race.arena.algorithm.sort.RadixSort;
import com.race.arena.model.SortRunResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CountingAndRadixEdgeCasesTest {

    @Test
    void countingSortHandlesNegativesViaOffset() {
        int[] input = {-5, 3, -8, 1, 0, -2, 7, -1};
        int[] expected = input.clone();
        Arrays.sort(expected);
        SortRunResult result = new CountingSort().runFast(input);
        assertArrayEquals(expected, result.sortedArray());
    }

    @Test
    void radixSortHandlesNegativesViaOffset() {
        int[] input = {-5, 3, -8, 1, 0, -2, 7, -1};
        int[] expected = input.clone();
        Arrays.sort(expected);
        SortRunResult result = new RadixSort().runFast(input);
        assertArrayEquals(expected, result.sortedArray());
    }

    @Test
    void countingSortRejectsExcessiveKeyRange() {
        int[] input = {0, Integer.MAX_VALUE};
        assertThrows(IllegalArgumentException.class, () -> new CountingSort().runFast(input));
    }
}
