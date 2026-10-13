package com.diff.studio.algolens.report;

import com.diff.studio.algolens.model.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Stage 6: Assembles and stores analysis reports.
 *
 * - Builds an {@link AnalysisReport} from raw findings.
 * - Generates overview text (rule-based; no LLM).
 * - Renders HTML and Markdown.
 * - Stores the last 20 reports in memory (LRU eviction).
 */
@Service
public class ReportService {

    private static final int MAX_STORED = 20;

    /** In-memory store of recent reports, keyed by ID. LRU eviction at MAX_STORED. */
    private final Map<String, AnalysisReport> store = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_STORED + 1, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, AnalysisReport> eldest) {
                    return size() > MAX_STORED;
                }
            });

    // -------------------------------------------------------------------------
    // Build report
    // -------------------------------------------------------------------------

    public AnalysisReport buildReport(String projectName,
                                       List<FindingWithComplexity> findings,
                                       List<ParsedFile> parsedFiles) {
        String id       = UUID.randomUUID().toString();
        String overview = generateOverview(projectName, findings, parsedFiles);
        List<String> notes    = generateNotes(findings);
        List<String> uncertain = detectUncertain(parsedFiles, findings);

        AnalysisReport report = new AnalysisReport(
                id, projectName, overview, findings, notes, uncertain, System.currentTimeMillis());
        store.put(id, report);
        return report;
    }

    public Optional<AnalysisReport> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    // -------------------------------------------------------------------------
    // Overview generator
    // -------------------------------------------------------------------------

    private String generateOverview(String projectName,
                                    List<FindingWithComplexity> findings,
                                    List<ParsedFile> parsedFiles) {
        int fileCount   = parsedFiles.size();
        int findingCount = findings.size();

        Set<String> algos = findings.stream()
                .map(f -> f.getFinding().getAlgorithm())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        String algoList = algos.isEmpty() ? "no well-known algorithms"
                : String.join(", ", algos);

        return String.format(
                "This project contains %d Java source file%s. " +
                "The analysis found %d algorithm or data-structure pattern%s. " +
                "Detected: %s. " +
                "The detection is rule-based. Confidence levels reflect pattern strength, not certainty. " +
                "Items in the uncertain section could not be classified with the current rules.",
                fileCount, fileCount == 1 ? "" : "s",
                findingCount, findingCount == 1 ? "" : "s",
                algoList);
    }

    // -------------------------------------------------------------------------
    // Notes generator
    // -------------------------------------------------------------------------

    private List<String> generateNotes(List<FindingWithComplexity> findings) {
        List<String> notes = new ArrayList<>();
        for (FindingWithComplexity fwc : findings) {
            String algo   = fwc.getFinding().getAlgorithm();
            String comp   = fwc.getTimeComplexity();

            if ((algo.equals("Bubble Sort") || algo.equals("Insertion Sort"))
                    && comp.contains("n^2")) {
                notes.add(fwc.getFinding().getFile() + " / " + fwc.getFinding().getMethod() +
                          ": " + algo + " is O(n^2). For large inputs, a merge sort or" +
                          " Collections.sort (Timsort, O(n log n)) would be faster.");
            }
            if (algo.equals("Dynamic Programming (tabulation)") && comp.contains("n^2")) {
                notes.add(fwc.getFinding().getFile() + " / " + fwc.getFinding().getMethod() +
                          ": This DP table appears to have O(n^2) time. Verify whether" +
                          " the state space can be reduced.");
            }
        }
        return notes;
    }

    // -------------------------------------------------------------------------
    // Uncertain sections
    // -------------------------------------------------------------------------

    private List<String> detectUncertain(List<ParsedFile> parsedFiles,
                                         List<FindingWithComplexity> findings) {
        Set<String> coveredFiles = findings.stream()
                .map(f -> f.getFinding().getFile())
                .collect(Collectors.toSet());

        List<String> uncertain = new ArrayList<>();
        for (ParsedFile pf : parsedFiles) {
            if (!coveredFiles.contains(pf.getRelativePath())) {
                uncertain.add(pf.getRelativePath() +
                              ": no patterns detected — may use domain-specific logic not covered by current rules.");
            }
        }
        return uncertain;
    }

    // -------------------------------------------------------------------------
    // Renderers
    // -------------------------------------------------------------------------

    /** Render the report as a Markdown string. */
    public String toMarkdown(AnalysisReport report) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC'").withZone(ZoneOffset.UTC);

        sb.append("# Code Reverse Report: ").append(report.getProjectName()).append("\n\n");
        sb.append("_Generated: ").append(fmt.format(Instant.ofEpochMilli(report.getAnalyzedAtMs()))).append("_\n\n");

        sb.append("## Overview\n\n").append(report.getOverview()).append("\n\n");

        sb.append("## Algorithms Found\n\n");
        sb.append("| Algorithm | Where | Complexity | Confidence |\n");
        sb.append("|-----------|-------|------------|------------|\n");
        for (FindingWithComplexity fwc : report.getFindings()) {
            Finding f = fwc.getFinding();
            sb.append("| ").append(escape(f.getAlgorithm()))
              .append(" | ").append(escape(f.getFile()))
              .append(f.getMethod().isBlank() ? "" : " / " + escape(f.getMethod()))
              .append(" | ").append(escape(fwc.getTimeComplexity()))
              .append(" | ").append(String.format("%.0f%%", f.getAdjustedConfidence() * 100))
              .append(" |\n");
        }
        sb.append("\n");

        sb.append("## Details\n\n");
        for (FindingWithComplexity fwc : report.getFindings()) {
            Finding f = fwc.getFinding();
            sb.append("### ").append(f.getAlgorithm()).append("\n\n");
            sb.append("**File:** ").append(f.getFile()).append("\n\n");
            if (!f.getMethod().isBlank()) {
                sb.append("**Method:** ").append(f.getMethod())
                  .append(" (lines ").append(f.getStartLine()).append("–").append(f.getEndLine()).append(")\n\n");
            }
            sb.append("**Evidence:** ").append(f.getEvidence()).append("\n\n");
            sb.append("**Complexity:** ").append(fwc.getTimeComplexity()).append("\n\n");
            sb.append("**Confidence:** ").append(String.format("%.0f%%", f.getAdjustedConfidence() * 100));
            if (!f.isLlmConfirmed()) sb.append(" (LLM rejected the detection)");
            sb.append("\n\n");
            if (f.getLlmExplanation() != null) {
                sb.append("**LLM note:** ").append(f.getLlmExplanation()).append("\n\n");
            }
        }

        if (!report.getNotes().isEmpty()) {
            sb.append("## Things Worth Noting\n\n");
            report.getNotes().forEach(n -> sb.append("- ").append(n).append("\n"));
            sb.append("\n");
        }

        if (!report.getUncertain().isEmpty()) {
            sb.append("## Uncertain\n\n");
            report.getUncertain().forEach(u -> sb.append("- ").append(u).append("\n"));
            sb.append("\n");
        }

        return sb.toString();
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("|", "\\|");
    }
}
