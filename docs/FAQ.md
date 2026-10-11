# Frequently Asked Questions

### Why does comparison-based sorting have an Omega(n log n) floor?
Any comparison sort can be modeled as a binary decision tree with n! leaves. The tree height must be at least log2(n!) = Omega(n log n).

### How does counting sort beat the lower bound?
Counting sort is a non-comparison algorithm that uses key indices directly. Its time complexity is O(n + k), which is linear when k = O(n).

### How is benchmark timing stabilized against JVM JIT optimization?
`BenchmarkService` runs multiple warm-up iterations to trigger HotSpot JIT compilation before collecting timed repetitions, then computes the median.
