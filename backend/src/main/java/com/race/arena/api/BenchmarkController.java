package com.race.arena.api;

import com.race.arena.dto.BenchmarkRequest;
import com.race.arena.model.BenchmarkRun;
import com.race.arena.repository.BenchmarkRunRepository;
import com.race.arena.service.BenchmarkService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;
    private final BenchmarkRunRepository repository;

    public BenchmarkController(BenchmarkService benchmarkService, BenchmarkRunRepository repository) {
        this.benchmarkService = benchmarkService;
        this.repository = repository;
    }

    @PostMapping("/benchmark")
    public List<BenchmarkService.BenchmarkSeries> benchmark(@Valid @RequestBody BenchmarkRequest request) {
        return benchmarkService.run(
                request.getAlgorithms(), request.getInputType(), request.getSizes(), request.getRepetitions());
    }

    @GetMapping("/history")
    public List<BenchmarkRun> history() {
        return repository.findAllByOrderByTimestampDesc(PageRequest.of(0, 20));
    }
}
