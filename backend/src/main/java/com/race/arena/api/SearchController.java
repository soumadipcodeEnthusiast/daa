package com.race.arena.api;

import com.race.arena.algorithm.SearchAlgorithm;
import com.race.arena.dto.SearchRequest;
import com.race.arena.dto.SearchResponse;
import com.race.arena.model.SearchRunResult;
import com.race.arena.service.AlgorithmRegistry;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    public static final int MAX_ARRAY_SIZE = 200;

    private final AlgorithmRegistry registry;

    public SearchController(AlgorithmRegistry registry) {
        this.registry = registry;
    }

    @PostMapping
    public SearchResponse search(@Valid @RequestBody SearchRequest request) {
        if (request.getArray().length > MAX_ARRAY_SIZE) {
            throw new IllegalArgumentException("Array too large for search trace: max " + MAX_ARRAY_SIZE + " elements");
        }
        SearchAlgorithm algo = registry.search(request.getAlgorithm());
        SearchRunResult result = algo.search(request.getArray(), request.getTarget());
        return new SearchResponse(result.foundIndex(), result.steps(), result.comparisons(), result.elapsedNanos());
    }
}
