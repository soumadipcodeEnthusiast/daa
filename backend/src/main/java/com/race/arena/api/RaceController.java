package com.race.arena.api;

import com.race.arena.algorithm.SortAlgorithm;
import com.race.arena.dto.RaceAlgorithmResult;
import com.race.arena.dto.RaceRequest;
import com.race.arena.dto.RaceResponse;
import com.race.arena.model.SortRunResult;
import com.race.arena.service.AlgorithmRegistry;
import com.race.arena.service.InputGenerator;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/race")
public class RaceController {

    public static final int MAX_SIZE = 100;

    private final AlgorithmRegistry registry;
    private final InputGenerator inputGenerator;

    public RaceController(AlgorithmRegistry registry, InputGenerator inputGenerator) {
        this.registry = registry;
        this.inputGenerator = inputGenerator;
    }

    @PostMapping
    public RaceResponse race(@Valid @RequestBody RaceRequest request) {
        if (request.getSize() <= 0 || request.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_SIZE + " for a race trace");
        }
        int[] input = inputGenerator.generate(request.getInputType(), request.getSize(), request.getSeed());

        List<RaceAlgorithmResult> results = new ArrayList<>();
        for (String algoId : request.getAlgorithms()) {
            SortAlgorithm algo = registry.sort(algoId);
            SortRunResult result = algo.runWithTrace(input);
            results.add(new RaceAlgorithmResult(algoId, result.steps(), result.stats(), result.sortedArray()));
        }
        return new RaceResponse(input, results);
    }
}
