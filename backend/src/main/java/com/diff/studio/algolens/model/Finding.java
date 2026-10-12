package com.diff.studio.algolens.model;

/**
 * One detected algorithm or data-structure pattern.
 *
 * confidence: 0.0 – 1.0. Rule-based detectors set it; the LLM step may lower it if it rejects.
 */
public class Finding {

    private final String algorithm;   // e.g. "Quick Sort"
    private final String file;        // relative path inside the analysed project
    private final String method;      // enclosing method name, or "" if top-level
    private final int    startLine;
    private final int    endLine;
    private final String evidence;    // short human-readable reason from the detector
    private final double confidence;  // 0.0 – 1.0

    // LLM fields — set later by LlmExplainService
    private String llmExplanation;    // 2-3 sentence plain text
    private boolean llmConfirmed = true; // false when LLM rejects the detection
    private double  adjustedConfidence;  // same as confidence until LLM runs

    public Finding(String algorithm, String file, String method,
                   int startLine, int endLine, String evidence, double confidence) {
        this.algorithm          = algorithm;
        this.file               = file;
        this.method             = method;
        this.startLine          = startLine;
        this.endLine            = endLine;
        this.evidence           = evidence;
        this.confidence         = confidence;
        this.adjustedConfidence = confidence;
    }

    // ---- getters ----

    public String  getAlgorithm()          { return algorithm; }
    public String  getFile()               { return file; }
    public String  getMethod()             { return method; }
    public int     getStartLine()          { return startLine; }
    public int     getEndLine()            { return endLine; }
    public String  getEvidence()           { return evidence; }
    public double  getConfidence()         { return confidence; }
    public String  getLlmExplanation()     { return llmExplanation; }
    public boolean isLlmConfirmed()        { return llmConfirmed; }
    public double  getAdjustedConfidence() { return adjustedConfidence; }

    // ---- setters used by LlmExplainService ----

    public void setLlmExplanation(String explanation)    { this.llmExplanation     = explanation; }
    public void setLlmConfirmed(boolean confirmed)       { this.llmConfirmed       = confirmed; }
    public void setAdjustedConfidence(double confidence) { this.adjustedConfidence = confidence; }
}
