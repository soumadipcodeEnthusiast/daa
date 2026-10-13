package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Binary Search.
 *
 * Signals:
 *   - Method name contains "binary" or "binarySearch"
 *   - Loop (or recursion) that maintains low/high/mid and halves the search space
 */
@Component
public class BinarySearchDetector implements AlgorithmDetector {

    @Override
    public List<Finding> detect(ParsedFile file) {
        List<Finding> findings = new ArrayList<>();
        for (MethodInfo m : file.getMethods()) {
            String snippet = m.getSnippet().toLowerCase();
            String nameL   = m.getName().toLowerCase();

            boolean nameBased = nameL.contains("binary") || nameL.contains("bsearch");
            boolean hasMid    = snippet.contains("mid") || snippet.contains("middle");
            boolean hasLowHigh = (snippet.contains("low") || snippet.contains("left")) &&
                                 (snippet.contains("high") || snippet.contains("right"));
            boolean halvingLoop = (m.getMaxLoopDepth() >= 1 || m.isRecursive()) && hasMid && hasLowHigh;

            if (nameBased && halvingLoop) {
                findings.add(new Finding(
                        "Binary Search", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Method name contains 'binary' and uses low/mid/high halving.",
                        0.90));
                continue;
            }
            if (halvingLoop) {
                findings.add(new Finding(
                        "Binary Search", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Halving loop with low/mid/high variables — consistent with binary search.",
                        0.68));
            }
        }
        return findings;
    }
}
