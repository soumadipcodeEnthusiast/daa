package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Insertion Sort.
 *
 * Heuristic: nested loop (or one loop + while/until) where an element is
 * shifted left until it reaches its sorted position.
 * Signals: shifting pattern (a[j] = a[j-1]) or method name "insertion".
 */
@Component
public class InsertionSortDetector implements AlgorithmDetector {

    @Override
    public List<Finding> detect(ParsedFile file) {
        List<Finding> findings = new ArrayList<>();
        for (MethodInfo m : file.getMethods()) {
            String snippet = m.getSnippet().toLowerCase();
            String nameL   = m.getName().toLowerCase();

            boolean nameBased = nameL.contains("insertion") || nameL.contains("insertsort");
            boolean shiftLeft = (snippet.contains("[j]") && snippet.contains("[j-1]") ||
                                 snippet.contains("[j+1]") && snippet.contains("[j]")) &&
                                snippet.contains("key");
            boolean hasTwoLevelLoop = m.getMaxLoopDepth() >= 2;

            if (nameBased && hasTwoLevelLoop) {
                findings.add(new Finding(
                        "Insertion Sort", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Method name suggests insertion sort with nested looping.",
                        0.85));
                continue;
            }
            if (shiftLeft && hasTwoLevelLoop) {
                findings.add(new Finding(
                        "Insertion Sort", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Shift-left pattern (a[j]=a[j-1]) with 'key' variable inside nested loop.",
                        0.72));
            }
        }
        return findings;
    }
}
