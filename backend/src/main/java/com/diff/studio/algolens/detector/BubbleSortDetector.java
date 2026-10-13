package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Bubble Sort.
 *
 * Heuristic: a method with at least 2-deep nested loops whose snippet
 * contains a swap-like pattern (temp variable or XOR swap) and a comparison
 * between adjacent elements. The method name is also checked as a shortcut.
 */
@Component
public class BubbleSortDetector implements AlgorithmDetector {

    private static final double CONFIDENCE_HIGH = 0.85;
    private static final double CONFIDENCE_LOW  = 0.55;

    @Override
    public List<Finding> detect(ParsedFile file) {
        List<Finding> findings = new ArrayList<>();
        for (MethodInfo m : file.getMethods()) {
            String snippet  = m.getSnippet().toLowerCase();
            String nameL    = m.getName().toLowerCase();
            boolean hasNestedLoop = m.getMaxLoopDepth() >= 2;

            // Strong signal: name mentions bubble
            if (nameL.contains("bubble") && hasNestedLoop) {
                findings.add(new Finding(
                        "Bubble Sort", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Method name contains 'bubble' and has nested loops.",
                        CONFIDENCE_HIGH));
                continue;
            }

            // Structural signal: nested loops + swap + comparison
            boolean hasSwap = snippet.contains("temp") || snippet.contains("swap");
            boolean hasComp = snippet.contains("[i]") && snippet.contains("[i+1]") ||
                              snippet.contains("[j]") && snippet.contains("[j+1]") ||
                              snippet.contains("[j]") && snippet.contains("[j-1]");

            if (hasNestedLoop && hasSwap && hasComp) {
                findings.add(new Finding(
                        "Bubble Sort", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Nested loops with adjacent-element swap and comparison.",
                        CONFIDENCE_LOW));
            }
        }
        return findings;
    }
}
