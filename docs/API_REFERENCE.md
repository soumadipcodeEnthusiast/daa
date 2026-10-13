# Code Reverse API Reference

The backend exposes a REST API on port `8095` (or mapped port in reverse proxy).

---

## 1. `POST /api/algolens/analyze`

Executes the static AST analysis pipeline and generates a full algorithm report.

### Request Formats

#### A. Multipart Form Data (`multipart/form-data`)
* `file` (optional, `MultipartFile`): Single `.java` file or a compressed `.zip` archive containing Java sources.
* `githubUrl` (optional, query param / form field): Public GitHub repository HTTPS URL (e.g. `https://github.com/owner/repo`).

#### B. JSON Body (`application/json`)
```json
{
  "githubUrl": "https://github.com/owner/repo"
}
```

### Response (`200 OK`)
```json
{
  "id": "ef9f3912-fe68-4db3-971e-6e983e7b6683",
  "projectName": "DemoAlgorithms",
  "overview": "This project contains 1 Java source file. The analysis found 10 algorithm or data-structure patterns...",
  "findings": [
    {
      "algorithm": "Quick Sort",
      "file": "DemoAlgorithms.java",
      "method": "quickSort",
      "startLine": 14,
      "endLine": 20,
      "evidence": "Method name contains 'quick' or 'partition' with pivot/recursion signal.",
      "confidence": 0.87,
      "timeComplexity": "O(n log n) average, O(n^2) worst-case estimated",
      "llmExplanation": null,
      "llmConfirmed": true
    }
  ],
  "notes": [
    "DemoAlgorithms.java / bubbleSort: Bubble Sort is O(n^2). For large inputs, a merge sort or Collections.sort (Timsort, O(n log n)) would be faster."
  ],
  "uncertain": [],
  "analyzedAtMs": 1791766435622
}
```

---

## 2. `GET /api/algolens/report/{id}/markdown`

Downloads the generated report in GitHub-Flavored Markdown format.

* **Response Header**: `Content-Disposition: attachment; filename="code-reverse-report-<id>.md"`
* **Content-Type**: `text/plain`
