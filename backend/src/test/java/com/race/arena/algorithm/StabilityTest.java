package com.race.arena.algorithm;

import com.race.arena.algorithm.sort.BubbleSort;
import com.race.arena.algorithm.sort.CountingSort;
import com.race.arena.algorithm.sort.InsertionSort;
import com.race.arena.algorithm.sort.MergeSort;
import com.race.arena.algorithm.sort.RadixSort;
import com.race.arena.model.SortRunResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Stability means: among elements sharing a key, their relative input order is preserved in the output.
 * We verify this behaviorally, not just by trusting the hardcoded meta flag, by attaching a "shadow"
 * array (initialized to the original indices [0..n-1]) that each algorithm mirrors every swap/overwrite
 * onto. Java passes arrays by reference, so the `tags` array below is mutated in place by the sort and
 * can be inspected afterwards: for any run of equal keys in the sorted output, their tags (original
 * positions) must come out strictly increasing.
 */
class StabilityTest {

    private static final int[] KEYS = {5, 3, 5, 2, 5, 1, 3, 5, 2, 0};

    @Test
    void bubbleSortIsStable() {
        int[] tags = identity(KEYS.length);
        SortRunResult result = new BubbleSort().runWithShadow(KEYS, tags);
        assertSortedCorrectly(result);
        assertStableOrder(result.sortedArray(), tags);
    }

    @Test
    void insertionSortIsStable() {
        int[] tags = identity(KEYS.length);
        SortRunResult result = new InsertionSort().runWithShadow(KEYS, tags);
        assertSortedCorrectly(result);
        assertStableOrder(result.sortedArray(), tags);
    }

    @Test
    void mergeSortIsStable() {
        int[] tags = identity(KEYS.length);
        SortRunResult result = new MergeSort().runWithShadow(KEYS, tags);
        assertSortedCorrectly(result);
        assertStableOrder(result.sortedArray(), tags);
    }

    @Test
    void countingSortIsStable() {
        int[] tags = identity(KEYS.length);
        SortRunResult result = new CountingSort().runWithShadow(KEYS, tags);
        assertSortedCorrectly(result);
        assertStableOrder(result.sortedArray(), tags);
    }

    @Test
    void radixSortIsStable() {
        int[] tags = identity(KEYS.length);
        SortRunResult result = new RadixSort().runWithShadow(KEYS, tags);
        assertSortedCorrectly(result);
        assertStableOrder(result.sortedArray(), tags);
    }

    private int[] identity(int n) {
        int[] tags = new int[n];
        for (int i = 0; i < n; i++) tags[i] = i;
        return tags;
    }

    private void assertSortedCorrectly(SortRunResult result) {
        int[] expected = KEYS.clone();
        java.util.Arrays.sort(expected);
        assertArrayEquals(expected, result.sortedArray());
    }

    /** For every run of equal keys in the sorted output, their original-index tags must be increasing. */
    private void assertStableOrder(int[] sortedKeys, int[] tags) {
        assertEquals(sortedKeys.length, tags.length);
        int i = 0;
        while (i < sortedKeys.length) {
            int j = i;
            while (j + 1 < sortedKeys.length && sortedKeys[j + 1] == sortedKeys[i]) j++;
            for (int k = i; k < j; k++) {
                assertTrue(tags[k] < tags[k + 1],
                        "stability violated for key=" + sortedKeys[i] + ": original index " + tags[k] +
                                " came after " + tags[k + 1] + " in the output");
            }
            i = j + 1;
        }
    }
}
