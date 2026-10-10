package com.race.arena.api;

import com.race.arena.algorithm.SortAlgorithm;
import com.race.arena.dto.TraceRequest;
import com.race.arena.dto.TraceResponse;
import com.race.arena.model.SortRunResult;
import com.race.arena.service.AlgorithmRegistry;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trace")
public class TraceController {

    public static final int MAX_ARRAY_SIZE = 200;

    private final AlgorithmRegistry registry;

    public TraceController(AlgorithmRegistry registry) {
        this.registry = registry;
    }

    @PostMapping
    public TraceResponse trace(@Valid @RequestBody TraceRequest request) {
        if (request.getArray().length > MAX_ARRAY_SIZE) {
            throw new IllegalArgumentException("Array too large for trace: max " + MAX_ARRAY_SIZE + " elements");
        }
        SortAlgorithm algo = registry.sort(request.getAlgorithm());
        SortRunResult result = algo.runWithTrace(request.getArray());
        return new TraceResponse(result.steps(), result.stats(), result.sortedArray());
    }
}
