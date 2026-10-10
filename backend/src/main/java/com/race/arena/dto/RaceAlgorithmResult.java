package com.race.arena.dto;

import com.race.arena.model.SortStats;
import com.race.arena.model.Step;

import java.util.List;

public record RaceAlgorithmResult(String algorithm, List<Step> steps, SortStats stats, int[] finalArray) {
}
