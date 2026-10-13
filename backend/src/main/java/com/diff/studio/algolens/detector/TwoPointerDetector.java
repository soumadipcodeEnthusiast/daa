package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Two-Pointer and Sliding Window patterns.
 *
 * Two-pointer signals:
 *   - Variables named left/right or lo/hi used inside a single loop
 *   - While loop that increments one pointer and decrements the other
 *
 * Sliding window signals:
 *   - Variables named window, windowSize, or start/end inside a loop
 */
@Component
public class TwoPointerDetector implements AlgorithmDetector {

    @Override
    public List<Finding> detect(ParsedFile file) {
        List<Finding> findings = new ArrayList<>();
        for (MethodInfo m : file.getMethods()) {
            String snippet = m.getSnippet().toLowerCase();
            String nameL   = m.getName().toLowerCase();
            boolean hasLoop = m.getMaxLoopDepth() >= 1;

            // Sliding window
            boolean windowKeyword = snippet.contains("window") ||
                                    snippet.contains("windowsize") ||
                                    nameL.contains("window") || nameL.contains("sliding");
            boolean startEnd = (snippet.contains("start") && snippet.contains("end"))
                               || (snippet.contains("left") && snippet.contains("right"));

            if (windowKeyword && hasLoop) {
                findings.add(new Finding(
                        "Sliding Window", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Window variable or sliding-window method name with a loop.",
                        0.78));
                continue;
            }

            // Two pointers
            boolean twoPointerName = nameL.contains("twopointer") || nameL.contains("two_pointer");
            boolean leftRight = (snippet.contains("left") && snippet.contains("right"))
                                || (snippet.contains("lo") && snippet.contains("hi"))
                                || (snippet.contains("i") && snippet.contains("j") && hasLoop);

            if (twoPointerName && hasLoop) {
                findings.add(new Finding(
                        "Two Pointers", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "Method name explicitly mentions two-pointer technique.",
                        0.85));
                continue;
            }
            if (leftRight && hasLoop && startEnd) {
                findings.add(new Finding(
                        "Two Pointers", file.getRelativePath(), m.getName(),
                        m.getStartLine(), m.getEndLine(),
                        "left/right or lo/hi pointer variables inside a loop.",
                        0.62));
            }
        }
        return findings;
    }
}
