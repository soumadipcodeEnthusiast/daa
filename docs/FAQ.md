# Frequently Asked Questions

### How does Code Reverse detect algorithms without executing the code?
Code Reverse uses Abstract Syntax Tree (AST) analysis via JavaParser. Instead of running untrusted bytecode in a sandbox, it parses source code into an AST and evaluates structural indicators: recursion patterns, nested iteration bounds, pivot choices, queue/stack operations, memoization maps, and variable mutation semantics.

### How accurate is the complexity estimation?
The complexity estimation is derived from static control-flow analysis and algorithm taxonomy matching. Known algorithmic archetypes (such as QuickSort or BinarySearch) are assigned proven mathematical bounds, while general code sections are analyzed for loop nesting depth and recursive branch factor.

### Can Code Reverse analyze private repositories?
The GitHub ingestion mode currently supports public repositories via HTTPS shallow clone (`git clone --depth 1`). For private code, users can download the repository as a `.zip` archive or upload single `.java` files directly.
