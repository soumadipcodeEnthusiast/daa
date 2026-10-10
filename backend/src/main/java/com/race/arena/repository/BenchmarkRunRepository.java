package com.race.arena.repository;

import com.race.arena.model.BenchmarkRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BenchmarkRunRepository extends JpaRepository<BenchmarkRun, Long> {
    List<BenchmarkRun> findAllByOrderByTimestampDesc(Pageable pageable);
}
