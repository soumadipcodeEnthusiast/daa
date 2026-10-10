package com.race.arena.model;

/**
 * Static theoretical facts about an algorithm, served via GET /api/algorithms
 * and rendered on the Theory Cards page.
 */
public record AlgorithmMeta(
        String id,
        String name,
        String category,
        String bestCase,
        String averageCase,
        String worstCase,
        String spaceComplexity,
        boolean stable,
        boolean inPlace,
        boolean comparisonBased,
        String recurrence,
        String theoremNote
) {
}
