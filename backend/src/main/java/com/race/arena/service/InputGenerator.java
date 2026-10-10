package com.race.arena.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class InputGenerator {

    public static final java.util.List<String> TYPES = java.util.List.of(
            "random", "sorted", "reversed", "nearly-sorted", "many-duplicates", "few-unique-large-range"
    );

    public int[] generate(String type, int size, long seed) {
        return generate(type, size, seed, null);
    }

    /**
     * keyRange, when given, overrides the value bound for "random" input (used by the
     * Counting Sort Breaks the Bound page to demonstrate the O(n+k) tradeoff independently of n).
     */
    public int[] generate(String type, int size, long seed, Integer keyRange) {
        if (size < 0) throw new IllegalArgumentException("size must be >= 0");
        if (!TYPES.contains(type)) {
            throw new IllegalArgumentException("Unknown inputType: " + type + ". Expected one of " + TYPES);
        }
        if (keyRange != null && keyRange < 1) {
            throw new IllegalArgumentException("keyRange must be >= 1");
        }
        Random rnd = new Random(seed);
        int[] a = new int[size];

        switch (type) {
            case "sorted" -> {
                for (int i = 0; i < size; i++) a[i] = i;
            }
            case "reversed" -> {
                for (int i = 0; i < size; i++) a[i] = size - i;
            }
            case "nearly-sorted" -> {
                for (int i = 0; i < size; i++) a[i] = i;
                int perturbations = Math.max(1, size / 20);
                for (int s = 0; s < perturbations && size > 1; s++) {
                    int x = rnd.nextInt(size), y = rnd.nextInt(size);
                    int tmp = a[x];
                    a[x] = a[y];
                    a[y] = tmp;
                }
            }
            case "many-duplicates" -> {
                for (int i = 0; i < size; i++) a[i] = rnd.nextInt(5);
            }
            case "few-unique-large-range" -> {
                int distinctCount = Math.min(5, Math.max(1, size));
                int[] distinctValues = new int[distinctCount];
                for (int i = 0; i < distinctCount; i++) {
                    distinctValues[i] = rnd.nextInt(1_000_000_000);
                }
                for (int i = 0; i < size; i++) a[i] = distinctValues[rnd.nextInt(distinctCount)];
            }
            default -> {
                int bound = keyRange != null ? keyRange : Math.max(size * 10, 1000);
                for (int i = 0; i < size; i++) a[i] = rnd.nextInt(bound);
            }
        }
        return a;
    }
}
