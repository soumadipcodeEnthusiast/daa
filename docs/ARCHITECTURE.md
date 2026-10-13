# Architecture & Pipeline

Code Reverse uses a multi-stage static analysis pipeline designed to inspect code synchronously without database overhead.

```
Input (File / Zip / GitHub)
          │
          ▼
   1. Ingestion Stage (IngestService)
      - Unpacks zips, clones shallow git repos via JGit, or handles single .java
          │
          ▼
   2. AST Parsing Stage (ParserService)
      - JavaParser generates Abstract Syntax Trees
      - Extracts methods, line ranges, class structures, and tokens
          │
          ▼
   3. Detection Stage (10 AlgorithmDetector Implementations)
      - Runs rule-based pattern matching over AST structures and control flows
          │
          ▼
   4. Complexity Estimation (ComplexityService)
      - Infers asymptotic upper and average bounds (Big-O) from AST loop depths and recursion
          │
          ▼
   5. Reporting & Caching (ReportService)
      - Synthesizes summary, notes, and uncertain sections
      - In-memory LRU report caching (20 recent reports)
      - Renders structured JSON and formatted Markdown
```
