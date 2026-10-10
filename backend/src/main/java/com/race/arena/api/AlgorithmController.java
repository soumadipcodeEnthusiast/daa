package com.race.arena.api;

import com.race.arena.algorithm.SearchAlgorithm;
import com.race.arena.algorithm.SortAlgorithm;
import com.race.arena.model.AlgorithmMeta;
import com.race.arena.service.AlgorithmRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmController {

    private final AlgorithmRegistry registry;

    public AlgorithmController(AlgorithmRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    public List<AlgorithmMeta> all() {
        List<AlgorithmMeta> metas = registry.allSorts().stream()
                .map(SortAlgorithm::meta)
                .collect(Collectors.toCollection(java.util.ArrayList::new));
        registry.allSearches().stream().map(SearchAlgorithm::meta).forEach(metas::add);
        return metas;
    }
}
