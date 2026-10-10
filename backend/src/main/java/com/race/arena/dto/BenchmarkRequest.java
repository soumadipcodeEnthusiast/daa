package com.race.arena.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BenchmarkRequest {

    @NotEmpty
    private List<String> algorithms;

    @NotNull
    private String inputType;

    @NotEmpty
    private List<Integer> sizes;

    private int repetitions = 5;

    /** Overrides the value range for "random" input; used by the counting-sort O(n+k) demo page. */
    private Integer keyRange;

    public List<String> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<String> algorithms) {
        this.algorithms = algorithms;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public List<Integer> getSizes() {
        return sizes;
    }

    public void setSizes(List<Integer> sizes) {
        this.sizes = sizes;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public Integer getKeyRange() {
        return keyRange;
    }

    public void setKeyRange(Integer keyRange) {
        this.keyRange = keyRange;
    }
}
