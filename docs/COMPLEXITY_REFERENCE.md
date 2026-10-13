# Asymptotic Complexity Reference

| Algorithm / Pattern | Best Time | Average Time | Worst Time | Space | Notes |
|---|---|---|---|---|---|
| **Binary Search** | $O(1)$ | $O(\log n)$ | $O(\log n)$ | $O(1)$ | Requires monotonic/sorted domain |
| **Two Pointers** | $O(1)$ | $O(n)$ | $O(n)$ | $O(1)$ | Converging index pointers |
| **Quick Sort** | $O(n \log n)$ | $O(n \log n)$ | $O(n^2)$ | $O(\log n)$ | Degrades to $O(n^2)$ with unbalanced pivot |
| **Merge Sort** | $O(n \log n)$ | $O(n \log n)$ | $O(n \log n)$ | $O(n)$ | Stable divide and conquer |
| **Bubble Sort** | $O(n)$ | $O(n^2)$ | $O(n^2)$ | $O(1)$ | Adjacent element swapping |
| **Insertion Sort** | $O(n)$ | $O(n^2)$ | $O(n^2)$ | $O(1)$ | Efficient on nearly sorted data |
| **Breadth-First Search (BFS)** | $O(V + E)$ | $O(V + E)$ | $O(V + E)$ | $O(V)$ | Level-by-level queue exploration |
| **Depth-First Search (DFS)** | $O(V + E)$ | $O(V + E)$ | $O(V + E)$ | $O(V)$ | Call stack or explicit recursion |
| **DP (Tabulation / Memo)** | Problem specific | State $\times$ transitions | State $\times$ transitions | $O(states)$ | Bottom-up array or memoized cache |
| **Heap (PriorityQueue)** | $O(1)$ peek | $O(\log n)$ insert/remove | $O(\log n)$ insert/remove | $O(n)$ | Binary heap tree operations |
