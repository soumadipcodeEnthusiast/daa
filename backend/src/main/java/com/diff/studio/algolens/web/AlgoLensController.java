package com.diff.studio.algolens.web;

import com.diff.studio.algolens.model.*;
import com.diff.studio.algolens.complexity.ComplexityService;
import com.diff.studio.algolens.detector.AlgorithmDetector;
import com.diff.studio.algolens.ingest.IngestService;
import com.diff.studio.algolens.llm.LlmExplainService;
import com.diff.studio.algolens.parser.ParserService;
import com.diff.studio.algolens.report.ReportService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for the AlgoLens analysis pipeline.
 *
 * Endpoints:
 *   POST /api/algolens/analyze          multipart: file or githubUrl param → JSON report
 *   GET  /api/algolens/report/{id}/markdown  download Markdown report
 */
@RestController
@RequestMapping("/api/algolens")
public class AlgoLensController {

    private final IngestService         ingestService;
    private final ParserService         parserService;
    private final List<AlgorithmDetector> detectors;
    private final ComplexityService     complexityService;
    private final LlmExplainService     llmExplainService;
    private final ReportService         reportService;

    public AlgoLensController(IngestService ingestService,
                               ParserService parserService,
                               List<AlgorithmDetector> detectors,
                               ComplexityService complexityService,
                               LlmExplainService llmExplainService,
                               ReportService reportService) {
        this.ingestService    = ingestService;
        this.parserService    = parserService;
        this.detectors        = detectors;
        this.complexityService = complexityService;
        this.llmExplainService = llmExplainService;
        this.reportService    = reportService;
    }

    // -------------------------------------------------------------------------
    // POST /api/algolens/analyze
    // Accepts multipart with optional 'file' part and optional 'githubUrl' param.
    // -------------------------------------------------------------------------

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReportDto> analyzeMultipart(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "githubUrl", required = false) String githubUrl) throws Exception {

        AnalysisReport report = runPipeline(file, githubUrl);
        return ResponseEntity.ok(new ReportDto(report));
    }

    @PostMapping(value = "/analyze", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDto> analyzeJson(@RequestBody AnalyzeRequest req) throws Exception {
        AnalysisReport report = runPipeline(null, req.getGithubUrl());
        return ResponseEntity.ok(new ReportDto(report));
    }

    // -------------------------------------------------------------------------
    // GET /api/algolens/report/{id}/markdown
    // -------------------------------------------------------------------------

    @GetMapping("/report/{id}/markdown")
    public ResponseEntity<String> downloadMarkdown(@PathVariable String id) {
        return reportService.findById(id)
                .map(report -> {
                    String md = reportService.toMarkdown(report);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"code-reverse-report-" + id.substring(0, 8) + ".md\"")
                            .contentType(MediaType.TEXT_PLAIN)
                            .body(md);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------------------------------------------------
    // Pipeline
    // -------------------------------------------------------------------------

    private AnalysisReport runPipeline(MultipartFile file, String githubUrl) throws IOException, GitAPIException {
        // 1. Ingest
        Map<String, String> sources;
        String projectName;

        if (file != null && !file.isEmpty()) {
            sources     = ingestService.ingestUpload(file);
            projectName = stripExtension(file.getOriginalFilename());
        } else if (githubUrl != null && !githubUrl.isBlank()) {
            sources     = ingestService.ingestGitHub(githubUrl);
            projectName = extractRepoName(githubUrl);
        } else {
            throw new IllegalArgumentException(
                    "Provide either a file upload or a githubUrl parameter.");
        }

        if (sources.isEmpty()) {
            throw new IllegalArgumentException(
                    "No Java source files found in the provided input.");
        }

        // 2. Parse
        List<ParsedFile> parsedFiles = parserService.parseAll(sources);

        // 3. Detect
        List<Finding> allFindings = new ArrayList<>();
        for (ParsedFile pf : parsedFiles) {
            for (AlgorithmDetector detector : detectors) {
                allFindings.addAll(detector.detect(pf));
            }
        }

        // 4. Complexity
        List<FindingWithComplexity> withComplexity = allFindings.stream()
                .map(f -> new FindingWithComplexity(f, complexityService.estimate(f, parsedFiles)))
                .collect(Collectors.toList());

        // 5. LLM (optional — skipped if no API key)
        llmExplainService.explain(allFindings);

        // 6. Build report
        return reportService.buildReport(projectName, withComplexity, parsedFiles);
    }

    private String stripExtension(String name) {
        if (name == null) return "unknown";
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private String extractRepoName(String url) {
        String[] parts = url.split("/");
        String last = parts[parts.length - 1];
        return last.endsWith(".git") ? last.substring(0, last.length() - 4) : last;
    }
}
