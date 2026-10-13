package com.diff.studio.algolens.complexity;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Stage 4: Rough Big-O label for each Finding.
 *
 * The estimate is based on:
 *   - Known algorithm names (hardcoded table)
 *   - Loop nesting depth of the enclosing method
 *   - Whether the method is recursive
 *
 * Every label is tagged "estimated" to make the uncertainty explicit.
 */
@Service
public class ComplexityService {

    private static final Map<String, String> KNOWN_COMPLEXITY = Map.ofEntries(
            Map.entry("Bubble Sort",                          "O(n^2) estimated"),
            Map.entry("Insertion Sort",                      "O(n^2) estimated"),
            Map.entry("Merge Sort",                          "O(n log n) estimated"),
            Map.entry("Quick Sort",                          "O(n log n) average, O(n^2) worst-case estimated"),
            Map.entry("Binary Search",                       "O(log n) estimated"),
            Map.entry("BFS",                                 "O(V + E) estimated"),
            Map.entry("DFS",                                 "O(V + E) estimated"),
            Map.entry("Dynamic Programming (memoization)",   "O(n * subproblems) estimated"),
            Map.entry("Dynamic Programming (tabulation)",    "O(n * subproblems) estimated"),
            Map.entry("Dynamic Programming",                 "O(n * subproblems) estimated"),
            Map.entry("Two Pointers",                        "O(n) estimated"),
            Map.entry("Sliding Window",                      "O(n) estimated"),
            Map.entry("HashMap / Map",                       "O(1) average per operation estimated"),
            Map.entry("PriorityQueue (Heap)",                "O(log n) per insertion/removal estimated"),
            Map.entry("Deque / Stack",                       "O(1) per push/pop estimated"),
            Map.entry("LRU Cache",                           "O(1) per get/put estimated")
    );

    /**
     * Return the estimated complexity label for a finding.
     * Falls back to a structural estimate when the algorithm name is not in the table.
     */
    public String estimate(Finding finding, List<ParsedFile> parsedFiles) {
        // Direct lookup by algorithm name
        String known = KNOWN_COMPLEXITY.get(finding.getAlgorithm());
        if (known != null) return known;

        // Structural fallback: find the matching method and use loop depth
        for (ParsedFile pf : parsedFiles) {
            if (!pf.getRelativePath().equals(finding.getFile())) continue;
            for (MethodInfo m : pf.getMethods()) {
                if (!m.getName().equals(finding.getMethod())) continue;
                return structuralEstimate(m);
            }
        }

        return "unknown complexity";
    }

    private String structuralEstimate(MethodInfo m) {
        int depth = m.getMaxLoopDepth();
        boolean recursive = m.isRecursive();

        if (depth == 0 && !recursive) return "O(1) estimated (no loops or recursion visible)";
        if (depth == 1 && !recursive) return "O(n) estimated (single loop)";
        if (depth == 2 && !recursive) return "O(n^2) estimated (two-level nested loop)";
        if (depth == 3 && !recursive) return "O(n^3) estimated (three-level nested loop)";
        if (depth > 3)                return "O(n^" + depth + ") estimated (deep loop nesting — verify manually)";
        if (recursive && depth <= 1)  return "O(n) or O(log n) estimated (recursion; exact shape unclear)";
        return "O(n^" + depth + ") estimated (nested loop with recursion)";
    }
}
