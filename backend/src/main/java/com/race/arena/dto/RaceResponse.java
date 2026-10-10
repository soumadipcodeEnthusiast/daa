package com.race.arena.dto;

import java.util.List;

public record RaceResponse(int[] inputArray, List<RaceAlgorithmResult> results) {
}
