package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects data structure usage:
 *   - HashMap / LinkedHashMap / TreeMap
 *   - PriorityQueue
 *   - Deque / ArrayDeque / Stack
 *   - LRU Cache (LinkedHashMap with removeEldestEntry, or custom node-based)
 *
 * This detector reports per-file findings rather than per-method,
 * because data structures are often declared as fields.
 */
@Component
public class DataStructureDetector implements AlgorithmDetector {

    @Override
    public List<Finding> detect(ParsedFile file) {
        List<Finding> findings = new ArrayList<>();
        String src = file.getSource().toLowerCase();

        checkLruCache(file, src, findings);
        checkHashMap(file, findings);
        checkPriorityQueue(file, findings);
        checkDequeStack(file, findings);

        return findings;
    }

    private void checkLruCache(ParsedFile file, String src, List<Finding> findings) {
        boolean usesLinkedHashMap = src.contains("linkedhashmap");
        boolean hasEldest = src.contains("removeeldestentry") || src.contains("eldest");
        boolean hasCapacity = src.contains("capacity") && src.contains("access");

        if (usesLinkedHashMap && (hasEldest || hasCapacity)) {
            findings.add(fileLevelFinding(
                    "LRU Cache", file,
                    "LinkedHashMap with removeEldestEntry or access-order eviction — LRU cache pattern.",
                    0.85));
        }
    }

    private void checkHashMap(ParsedFile file, List<Finding> findings) {
        boolean hasHashMap = file.getSource().contains("HashMap") ||
                             file.getSource().contains("LinkedHashMap") ||
                             file.getSource().contains("TreeMap");
        if (hasHashMap) {
            findings.add(fileLevelFinding(
                    "HashMap / Map", file,
                    "One or more Map implementations (HashMap, LinkedHashMap, TreeMap) instantiated.",
                    0.75));
        }
    }

    private void checkPriorityQueue(ParsedFile file, List<Finding> findings) {
        boolean hasPQ = file.getSource().contains("PriorityQueue");
        if (hasPQ) {
            findings.add(fileLevelFinding(
                    "PriorityQueue (Heap)", file,
                    "PriorityQueue instantiated — likely used as a min-heap or max-heap.",
                    0.80));
        }
    }

    private void checkDequeStack(ParsedFile file, List<Finding> findings) {
        String src = file.getSource();
        boolean hasDeque = src.contains("Deque") || src.contains("ArrayDeque");
        boolean hasStack = src.contains("new Stack");
        if (hasDeque || hasStack) {
            findings.add(fileLevelFinding(
                    "Deque / Stack", file,
                    (hasDeque ? "Deque/ArrayDeque" : "Stack") + " instantiated — used as a stack or double-ended queue.",
                    0.75));
        }
    }

    private Finding fileLevelFinding(String algo, ParsedFile file, String evidence, double confidence) {
        return new Finding(algo, file.getRelativePath(), "",
                1, countLines(file.getSource()), evidence, confidence);
    }

    private int countLines(String src) {
        return (int) src.lines().count();
    }
}
