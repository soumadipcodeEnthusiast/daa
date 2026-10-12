package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Merge Sort.
 *
 * Merge sort is recursive and splits the input in half.
 * Signals:
 *   - Method is recursive AND snippet contains "mid" or "merge"
 *   - Or method name contains "merge"
 */
@Component
public class MergeSortDetector implements AlgorithmDetector {

    @Override
    public List<Finding> detect(ParsedFile file) {
        List<Finding> findings = new ArrayList<>();
        for (MethodInfo m : file.getMethods()) {
            String snippet = m.getSnippet().toLowerCase();
            String nameL   = m.getName().toLowerCase();

            boolean nameBased = nameL.contains("merge") || nameL.contains("mergesort");
            boolean splitSignal = m.isRecursive() &&
                    (snippet.contains("mid") || snippet.contains("middle") || snippet.contains("/ 2"));
            boolean mergeCall = snippet.contains("merge(") || snippet.contains("mergesort(");

            if (nameBased && (m.isRecursive() || mergeCall)) {
                findings.add(new Finding(
                        "Merge Sort", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Method name contains 'merge' and the method is recursive or calls a merge step.",
                        0.88));
                continue;
            }
            if (splitSignal && mergeCall) {
                findings.add(new Finding(
                        "Merge Sort", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Recursive method that splits at midpoint and calls a merge step.",
                        0.75));
            }
        }
        return findings;
    }
}
