package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Quick Sort.
 *
 * Signals:
 *   - Method name contains "quick" or "partition"
 *   - Recursive method that mentions a "pivot" and contains swap logic
 */
@Component
public class QuickSortDetector implements AlgorithmDetector {

    @Override
    public List<Finding> detect(ParsedFile file) {
        List<Finding> findings = new ArrayList<>();
        for (MethodInfo m : file.getMethods()) {
            String snippet = m.getSnippet().toLowerCase();
            String nameL   = m.getName().toLowerCase();

            boolean nameBased  = nameL.contains("quick") || nameL.contains("partition");
            boolean hasPivot   = snippet.contains("pivot");
            boolean hasSwap    = snippet.contains("swap") || snippet.contains("temp");
            boolean recursive  = m.isRecursive();
            boolean partCall   = snippet.contains("partition(");

            if (nameBased && (hasPivot || recursive)) {
                findings.add(new Finding(
                        "Quick Sort", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Method name contains 'quick' or 'partition' with pivot/recursion signal.",
                        0.87));
                continue;
            }
            if (recursive && hasPivot && hasSwap) {
                findings.add(new Finding(
                        "Quick Sort", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Recursive method with pivot and swap — consistent with quicksort.",
                        0.70));
            } else if (partCall && recursive) {
                findings.add(new Finding(
                        "Quick Sort", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Recursive sort that calls a partition step.",
                        0.65));
            }
        }
        return findings;
    }
}
