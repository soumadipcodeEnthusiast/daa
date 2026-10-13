# Code Reverse

A static analysis and reverse-engineering tool for Java source code. **Code Reverse** inspects Java source files—whether uploaded directly as `.java` files, compressed in `.zip` archives, or fetched from public GitHub repositories—and automatically identifies:

1. **Which algorithms and data structures are used** (Sorting, Searching, Graph Traversal, Dynamic Programming, Heaps, HashMaps, Two Pointers, etc.)
2. **Where they are located** (file path, enclosing method, exact line ranges)
3. **Why it was detected** (concrete evidence from AST structure and code signals)
4. **Estimated asymptotic time complexity** ($O(n \log n)$, $O(\log n)$, $O(n^2)$, $O(V + E)$, etc.)
5. **Detailed, downloadable Markdown reports**

---

## Architecture

```
┌────────────────────────────────────────┐         ┌────────────────────────────────────────┐
│     Frontend UI (localhost:5173)       │  HTTP   │      Spring Boot Backend (:8095)       │
│                                        │ ──────► │                                        │
│  React 19 + TypeScript + Vite          │  /api/* │  Pipeline:                             │
│  Tailwind CSS + Custom Design System   │         │   1. IngestService (File/Zip/Git)      │
│  - Mode Selection (File / GitHub)      │ ◄────── │   2. ParserService (JavaParser AST)    │
│  - 1-Click Demo Showcase Loader        │  JSON   │   3. 10 Algorithm Detectors            │
│  - Structured Findings & Complexity    │         │   4. ComplexityService (Estimations)   │
│  - Markdown Export                     │         │   5. ReportService (LRU Store & MD)    │
└────────────────────────────────────────┘         └────────────────────────────────────────┘
```

---

## Supported Detectors

| Category | Algorithm / Structure | Complexity | Detection Signals |
|---|---|---|---|
| **Divide & Conquer** | Quick Sort | $O(n \log n)$ avg, $O(n^2)$ worst | Pivot partitioning, recursive array division |
| **Divide & Conquer** | Merge Sort | $O(n \log n)$ | Midpoint division, sub-array recursive merge |
| **Searching** | Binary Search | $O(\log n)$ | Boundary mid calculation, low/high interval halving |
| **Pointers** | Two Pointers | $O(n)$ | Opposing left/right indices convergence |
| **Sorting** | Bubble Sort | $O(n^2)$ | Nested loops with adjacent element swapping |
| **Sorting** | Insertion Sort | $O(n^2)$ | Key insertion into sorted prefix with element shifting |
| **Graph Traversal** | Breadth-First Search (BFS) | $O(V + E)$ | Queue-driven node expansion, visited state set/array |
| **Graph Traversal** | Depth-First Search (DFS) | $O(V + E)$ | Recursive graph/tree traversal, visited tracking |
| **Dynamic Prog.** | DP (Tabulation & Memoization) | Variable ($O(m \cdot n)$, etc.) | Multi-dimensional DP tables, recursive state memoization |
| **Data Structures** | Core Collections | $O(1)$ / $O(\log n)$ | PriorityQueue (Min/Max Heaps), HashMap, TreeMap, Sets |

---

## Quick Start

### Prerequisites
* **Java 21** or later
* **Maven 3.8+**
* **Node.js 18+** and **npm**

### 1. Run the Backend

```bash
cd backend
mvn clean test
mvn spring-boot:run
```
The backend starts on `http://localhost:8095`.

### 2. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173` in your browser.

---

## Sample Demo File

A self-contained showcase Java file containing implementations of classical algorithms is provided at [`DemoAlgorithms.java`](DemoAlgorithms.java). You can either upload it manually in the web UI or click the **"Load Demo Sample"** button on the interface to test all 10 detectors with a single click.

---

## API Endpoints

* `POST /api/algolens/analyze` — Multipart (`file` or `githubUrl`) or JSON payload returning full analysis report.
* `GET /api/algolens/report/{id}/markdown` — Downloads the generated Markdown report.
