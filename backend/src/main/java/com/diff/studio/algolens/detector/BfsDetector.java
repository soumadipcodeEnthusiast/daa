package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Breadth-First Search (BFS).
 *
 * Signals:
 *   - Method name contains "bfs" or "breadthFirst"
 *   - Uses a Queue/Deque/ArrayDeque AND a visited set/array, with a loop
 */
@Component
public class BfsDetector implements AlgorithmDetector {

    @Override
    public List<Finding> detect(ParsedFile file) {
        List<Finding> findings = new ArrayList<>();
        for (MethodInfo m : file.getMethods()) {
            String snippet    = m.getSnippet().toLowerCase();
            String nameL      = m.getName().toLowerCase();
            List<String> cols = m.getCollectionTypes();

            boolean nameBased     = nameL.contains("bfs") || nameL.contains("breadthfirst")
                                    || nameL.contains("breadth");
            boolean hasQueue      = snippet.contains("queue") || snippet.contains("arraydeque")
                                    || snippet.contains("linkedlist")
                                    || cols.stream().anyMatch(c -> c.toLowerCase().contains("queue")
                                                               || c.equals("ArrayDeque")
                                                               || c.equals("LinkedList"));
            boolean hasVisited    = snippet.contains("visited") || snippet.contains("seen");
            boolean hasLoop       = m.getMaxLoopDepth() >= 1;

            if (nameBased && hasQueue) {
                findings.add(new Finding(
                        "BFS", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Method name suggests BFS and uses a queue.",
                        0.90));
                continue;
            }
            if (hasQueue && hasVisited && hasLoop) {
                findings.add(new Finding(
                        "BFS", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Queue + visited marker + loop — consistent with BFS traversal.",
                        0.75));
            }
        }
        return findings;
    }
}
