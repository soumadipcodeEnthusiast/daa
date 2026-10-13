# Presentation Guide

## 5-Minute Technical Presentation

### Slide 1: Introduction & Problem Statement
* Reverse-engineering code manually is time-consuming.
* Developers and reviewers need instant insight into algorithmic complexity, data structures, and optimization opportunities.
* **Code Reverse**: Automated AST-driven detection pipeline with $0$ runtime execution overhead.

### Slide 2: Ingestion & AST Parsing
* Flexible input: Single `.java` files, `.zip` packages, or public GitHub URLs.
* Uses **JavaParser** to build a comprehensive AST without bytecode compilation.

### Slide 3: Rule-Based Detection Engine
* 10 specialized detectors covering Sorting, Searching, Graphs, DP, Two Pointers, and Data Structures.
* Explains *why* an algorithm is flagged using concrete syntactic evidence and line ranges.

### Slide 4: Asymptotic Complexity & Advisory Notes
* Maps detected patterns to theoretical Big-O bounds.
* Generates actionable advisory warnings (e.g. flagging $O(n^2)$ Bubble Sort vs $O(n \log n)$ collections sort).

### Slide 5: Live Demonstration
* Uploading `DemoAlgorithms.java` via the UI.
* Instant report generation with 10 findings, line numbers, and Markdown download.
