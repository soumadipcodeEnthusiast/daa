package com.diff.studio.algolens.web;

import java.util.List;

/**
 * Request body for POST /api/algolens/analyze (JSON path).
 * Exactly one of {@code githubUrl} or the multipart {@code file} field must be set.
 * When used with multipart, the file is sent as a form field.
 */
public class AnalyzeRequest {

    private String githubUrl;

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
}
