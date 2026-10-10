package com.race.arena.algorithm;

import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SortRunResult;

public interface SortAlgorithm {

    String id();

    String displayName();

    AlgorithmMeta meta();

    /** Full step-by-step trace, for the Race Arena / Trace views. Use only on small arrays (<=200). */
    SortRunResult runWithTrace(int[] input);

    /** No step list is recorded; counters + elapsed time only. Used by the Benchmark Lab at large n. */
    SortRunResult runFast(int[] input);
}
