package com.race.arena.dto;

import com.race.arena.model.Step;

import java.util.List;

public record SearchResponse(int foundIndex, List<Step> steps, long comparisons, long elapsedNanos) {
}
