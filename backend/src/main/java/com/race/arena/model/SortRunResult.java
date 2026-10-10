package com.race.arena.model;

import java.util.List;

public record SortRunResult(int[] sortedArray, List<Step> steps, SortStats stats) {
}
