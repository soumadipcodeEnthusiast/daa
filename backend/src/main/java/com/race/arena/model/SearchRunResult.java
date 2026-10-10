package com.race.arena.model;

import java.util.List;

public record SearchRunResult(int foundIndex, List<Step> steps, long comparisons, long elapsedNanos) {
}
