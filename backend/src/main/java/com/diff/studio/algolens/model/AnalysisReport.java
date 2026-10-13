package com.diff.studio.algolens.model;

import java.util.List;

/**
 * The complete analysis result for one submitted project.
 * ReportService builds this from the pipeline outputs.
 */
public class AnalysisReport {

    private final String       id;            // UUID used in /report/{id}/markdown
    private final String       projectName;   // derived from repo/file name
    private final String       overview;      // 4-6 sentence plain text
    private final List<FindingWithComplexity> findings;
    private final List<String> notes;         // neutral suggestions
    private final List<String> uncertain;     // parts the tool could not classify
    private final long         analyzedAtMs;

    public AnalysisReport(String id, String projectName, String overview,
                          List<FindingWithComplexity> findings,
                          List<String> notes, List<String> uncertain,
                          long analyzedAtMs) {
        this.id            = id;
        this.projectName   = projectName;
        this.overview      = overview;
        this.findings      = findings;
        this.notes         = notes;
        this.uncertain     = uncertain;
        this.analyzedAtMs  = analyzedAtMs;
    }

    public String                     getId()           { return id; }
    public String                     getProjectName()  { return projectName; }
    public String                     getOverview()     { return overview; }
    public List<FindingWithComplexity> getFindings()    { return findings; }
    public List<String>               getNotes()        { return notes; }
    public List<String>               getUncertain()    { return uncertain; }
    public long                       getAnalyzedAtMs() { return analyzedAtMs; }
}
