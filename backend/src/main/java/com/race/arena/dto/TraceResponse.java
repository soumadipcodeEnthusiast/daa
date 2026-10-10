package com.race.arena.dto;

import com.race.arena.model.SortStats;
import com.race.arena.model.Step;

import java.util.List;

public record TraceResponse(List<Step> steps, SortStats stats, int[] finalArray) {
}
