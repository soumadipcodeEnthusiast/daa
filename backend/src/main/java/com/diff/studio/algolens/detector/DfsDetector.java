package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Depth-First Search (DFS).
 *
 * Signals:
 *   - Method name contains "dfs" or "depthFirst"
 *   - Recursive method with a visited set/array, or uses an explicit Stack
 */
@Component
public class DfsDetector implements AlgorithmDetector {

    @Override
    public List<Finding> detect(ParsedFile file) {
        List<Finding> findings = new ArrayList<>();
        for (MethodInfo m : file.getMethods()) {
            String snippet    = m.getSnippet().toLowerCase();
            String nameL      = m.getName().toLowerCase();
            List<String> cols = m.getCollectionTypes();

            boolean nameBased   = nameL.contains("dfs") || nameL.contains("depthfirst")
                                  || nameL.contains("depth");
            boolean hasVisited  = snippet.contains("visited") || snippet.contains("seen");
            boolean hasStack    = snippet.contains("stack") ||
                                  cols.stream().anyMatch(c -> c.equals("Stack") || c.equals("ArrayDeque"));
            boolean recursive   = m.isRecursive();

            if (nameBased && (recursive || hasStack)) {
                findings.add(new Finding(
                        "DFS", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Method name suggests DFS; uses recursion or an explicit stack.",
                        0.90));
                continue;
            }
            if (recursive && hasVisited) {
                findings.add(new Finding(
                        "DFS", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Recursive method with visited tracking — consistent with DFS.",
                        0.70));
            } else if (hasStack && hasVisited) {
                findings.add(new Finding(
                        "DFS", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Explicit Stack with visited tracking — iterative DFS pattern.",
                        0.68));
            }
        }
        return findings;
    }
}
