package com.race.arena.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class BenchmarkRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant timestamp;
    private String algorithm;
    private String inputType;
    private int size;
    private int repetitions;
    private long medianNanos;
    private long comparisons;

    public BenchmarkRun() {
    }

    public BenchmarkRun(String algorithm, String inputType, int size, int repetitions, long medianNanos, long comparisons) {
        this.timestamp = Instant.now();
        this.algorithm = algorithm;
        this.inputType = inputType;
        this.size = size;
        this.repetitions = repetitions;
        this.medianNanos = medianNanos;
        this.comparisons = comparisons;
    }

    public Long getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getInputType() {
        return inputType;
    }

    public int getSize() {
        return size;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public long getMedianNanos() {
        return medianNanos;
    }

    public long getComparisons() {
        return comparisons;
    }
}
