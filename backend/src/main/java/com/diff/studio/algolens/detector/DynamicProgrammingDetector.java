package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Dynamic Programming (memoization or tabulation).
 *
 * Signals for memoization:
 *   - Recursive method + map/array used for caching (memo, cache, dp)
 *
 * Signals for tabulation:
 *   - Nested loops + dp array filled bottom-up
 */
@Component
public class DynamicProgrammingDetector implements AlgorithmDetector {

    @Override
    public List<Finding> detect(ParsedFile file) {
        List<Finding> findings = new ArrayList<>();
        for (MethodInfo m : file.getMethods()) {
            String snippet    = m.getSnippet().toLowerCase();
            String nameL      = m.getName().toLowerCase();
            List<String> cols = m.getCollectionTypes();

            boolean hasMemoKeyword  = snippet.contains("memo") || snippet.contains("cache");
            boolean hasDpArray      = snippet.contains("dp[") || snippet.contains("dp =");
            boolean hasTabKeyword   = nameL.contains("dp") || nameL.contains("dynamic")
                                      || nameL.contains("tabulate");
            boolean recursive       = m.isRecursive();
            boolean hasMap          = cols.stream().anyMatch(c -> c.contains("Map"));
            boolean nestedLoop      = m.getMaxLoopDepth() >= 2;

            // Memoization: recursive + cache structure
            if (recursive && (hasMemoKeyword || hasMap && hasDpArray)) {
                findings.add(new Finding(
                        "Dynamic Programming (memoization)", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Recursive method with memo/cache structure — top-down DP.",
                        0.80));
                continue;
            }

            // Tabulation: nested loops + dp array
            if (hasDpArray && nestedLoop) {
                findings.add(new Finding(
                        "Dynamic Programming (tabulation)", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Nested loops filling a dp[] array — bottom-up DP.",
                        0.78));
                continue;
            }

            // Name-based fallback
            if (hasTabKeyword && (hasDpArray || hasMemoKeyword)) {
                findings.add(new Finding(
                        "Dynamic Programming", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Method name or dp[] array suggest dynamic programming.",
                        0.60));
            }
        }
        return findings;
    }
}
