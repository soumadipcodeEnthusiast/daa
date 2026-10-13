package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.ParsedFile;

import java.util.List;

/**
 * Contract for every algorithm/data-structure detector.
 *
 * Each implementation is a Spring bean and is injected as a
 * {@code List<AlgorithmDetector>} into the analysis pipeline.
 */
public interface AlgorithmDetector {

    /**
     * Inspect one parsed file and return all findings.
     * Return an empty list — not null — when nothing is found.
     */
    List<Finding> detect(ParsedFile file);
}
