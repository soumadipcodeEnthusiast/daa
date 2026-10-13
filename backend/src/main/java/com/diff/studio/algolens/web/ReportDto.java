package com.diff.studio.algolens.web;

import com.diff.studio.algolens.model.AnalysisReport;
import com.diff.studio.algolens.model.FindingWithComplexity;
import com.diff.studio.algolens.model.Finding;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JSON-serialisable view of an AnalysisReport.
 * Flattens the nested model into a structure that is straightforward to consume from the frontend.
 */
public class ReportDto {

    private final String           id;
    private final String           projectName;
    private final String           overview;
    private final List<FindingDto> findings;
    private final List<String>     notes;
    private final List<String>     uncertain;
    private final long             analyzedAtMs;

    public ReportDto(AnalysisReport r) {
        this.id           = r.getId();
        this.projectName  = r.getProjectName();
        this.overview     = r.getOverview();
        this.findings     = r.getFindings().stream().map(FindingDto::new).collect(Collectors.toList());
        this.notes        = r.getNotes();
        this.uncertain    = r.getUncertain();
        this.analyzedAtMs = r.getAnalyzedAtMs();
    }

    public String           getId()           { return id; }
    public String           getProjectName()  { return projectName; }
    public String           getOverview()     { return overview; }
    public List<FindingDto> getFindings()     { return findings; }
    public List<String>     getNotes()        { return notes; }
    public List<String>     getUncertain()    { return uncertain; }
    public long             getAnalyzedAtMs() { return analyzedAtMs; }

    // ---- Inner DTO ----

    public static class FindingDto {
        private final String  algorithm;
        private final String  file;
        private final String  method;
        private final int     startLine;
        private final int     endLine;
        private final String  evidence;
        private final double  confidence;
        private final String  timeComplexity;
        private final String  llmExplanation;
        private final boolean llmConfirmed;

        FindingDto(FindingWithComplexity fwc) {
            Finding f        = fwc.getFinding();
            this.algorithm      = f.getAlgorithm();
            this.file           = f.getFile();
            this.method         = f.getMethod();
            this.startLine      = f.getStartLine();
            this.endLine        = f.getEndLine();
            this.evidence       = f.getEvidence();
            this.confidence     = Math.round(f.getAdjustedConfidence() * 100.0) / 100.0;
            this.timeComplexity = fwc.getTimeComplexity();
            this.llmExplanation = f.getLlmExplanation();
            this.llmConfirmed   = f.isLlmConfirmed();
        }

        public String  getAlgorithm()     { return algorithm; }
        public String  getFile()          { return file; }
        public String  getMethod()        { return method; }
        public int     getStartLine()     { return startLine; }
        public int     getEndLine()       { return endLine; }
        public String  getEvidence()      { return evidence; }
        public double  getConfidence()    { return confidence; }
        public String  getTimeComplexity(){ return timeComplexity; }
        public String  getLlmExplanation(){ return llmExplanation; }
        public boolean isLlmConfirmed()   { return llmConfirmed; }
    }
}
