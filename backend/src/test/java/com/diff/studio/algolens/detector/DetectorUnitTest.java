package com.diff.studio.algolens.detector;

import com.diff.studio.algolens.model.Finding;
import com.diff.studio.algolens.model.ParsedFile;
import com.diff.studio.algolens.parser.ParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for all 10 detectors.
 *
 * Each test:
 *   - Uses a small, real Java snippet
 *   - Verifies the detector finds the pattern
 *   - Verifies unrelated code is not flagged
 */
class DetectorUnitTest {

    private ParserService parser;

    @BeforeEach
    void setUp() {
        parser = new ParserService();
    }

    // -------------------------------------------------------------------------
    // Bubble Sort
    // -------------------------------------------------------------------------

    private static final String BUBBLE_SORT_CODE = """
            public class Sorter {
                public void bubbleSort(int[] arr) {
                    int n = arr.length;
                    for (int i = 0; i < n - 1; i++) {
                        for (int j = 0; j < n - i - 1; j++) {
                            if (arr[j] > arr[j + 1]) {
                                int temp = arr[j];
                                arr[j] = arr[j + 1];
                                arr[j + 1] = temp;
                            }
                        }
                    }
                }
            }
            """;

    @Test
    void bubbleSortDetector_findsRealBubbleSort() {
        ParsedFile pf = parser.parseOne("Sorter.java", BUBBLE_SORT_CODE);
        List<Finding> findings = new BubbleSortDetector().detect(pf);
        assertThat(findings).isNotEmpty();
        assertThat(findings.get(0).getAlgorithm()).isEqualTo("Bubble Sort");
    }

    @Test
    void bubbleSortDetector_ignoresSimpleMethod() {
        String simple = "public class Foo { public int add(int a, int b) { return a + b; } }";
        ParsedFile pf = parser.parseOne("Foo.java", simple);
        assertThat(new BubbleSortDetector().detect(pf)).isEmpty();
    }

    // -------------------------------------------------------------------------
    // Insertion Sort
    // -------------------------------------------------------------------------

    private static final String INSERTION_SORT_CODE = """
            public class Sorter {
                public void insertionSort(int[] arr) {
                    for (int i = 1; i < arr.length; i++) {
                        int key = arr[i];
                        int j = i - 1;
                        while (j >= 0 && arr[j] > key) {
                            arr[j + 1] = arr[j];
                            j--;
                        }
                        arr[j + 1] = key;
                    }
                }
            }
            """;

    @Test
    void insertionSortDetector_findsRealInsertionSort() {
        ParsedFile pf = parser.parseOne("Sorter.java", INSERTION_SORT_CODE);
        List<Finding> findings = new InsertionSortDetector().detect(pf);
        assertThat(findings).isNotEmpty();
        assertThat(findings.get(0).getAlgorithm()).isEqualTo("Insertion Sort");
    }

    // -------------------------------------------------------------------------
    // Merge Sort
    // -------------------------------------------------------------------------

    private static final String MERGE_SORT_CODE = """
            public class MergeSorter {
                public void mergeSort(int[] arr, int l, int r) {
                    if (l < r) {
                        int mid = (l + r) / 2;
                        mergeSort(arr, l, mid);
                        mergeSort(arr, mid + 1, r);
                        merge(arr, l, mid, r);
                    }
                }
                private void merge(int[] arr, int l, int m, int r) {}
            }
            """;

    @Test
    void mergeSortDetector_findsRealMergeSort() {
        ParsedFile pf = parser.parseOne("MergeSorter.java", MERGE_SORT_CODE);
        List<Finding> findings = new MergeSortDetector().detect(pf);
        assertThat(findings).isNotEmpty();
    }

    // -------------------------------------------------------------------------
    // Quick Sort
    // -------------------------------------------------------------------------

    private static final String QUICK_SORT_CODE = """
            public class QuickSorter {
                public void quickSort(int[] arr, int low, int high) {
                    if (low < high) {
                        int pi = partition(arr, low, high);
                        quickSort(arr, low, pi - 1);
                        quickSort(arr, pi + 1, high);
                    }
                }
                private int partition(int[] arr, int low, int high) {
                    int pivot = arr[high];
                    int i = low - 1;
                    for (int j = low; j < high; j++) {
                        if (arr[j] <= pivot) { i++; int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp; }
                    }
                    int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
                    return i + 1;
                }
            }
            """;

    @Test
    void quickSortDetector_findsRealQuickSort() {
        ParsedFile pf = parser.parseOne("QuickSorter.java", QUICK_SORT_CODE);
        List<Finding> findings = new QuickSortDetector().detect(pf);
        assertThat(findings).isNotEmpty();
    }

    // -------------------------------------------------------------------------
    // Binary Search
    // -------------------------------------------------------------------------

    private static final String BINARY_SEARCH_CODE = """
            public class Search {
                public int binarySearch(int[] arr, int target) {
                    int low = 0, high = arr.length - 1;
                    while (low <= high) {
                        int mid = low + (high - low) / 2;
                        if (arr[mid] == target) return mid;
                        else if (arr[mid] < target) low = mid + 1;
                        else high = mid - 1;
                    }
                    return -1;
                }
            }
            """;

    @Test
    void binarySearchDetector_findsRealBinarySearch() {
        ParsedFile pf = parser.parseOne("Search.java", BINARY_SEARCH_CODE);
        List<Finding> findings = new BinarySearchDetector().detect(pf);
        assertThat(findings).isNotEmpty();
        assertThat(findings.get(0).getAlgorithm()).isEqualTo("Binary Search");
    }

    // -------------------------------------------------------------------------
    // BFS
    // -------------------------------------------------------------------------

    private static final String BFS_CODE = """
            import java.util.*;
            public class Graph {
                public void bfs(int start, boolean[] visited, List<List<Integer>> adj) {
                    Queue<Integer> queue = new LinkedList<>();
                    visited[start] = true;
                    queue.add(start);
                    while (!queue.isEmpty()) {
                        int node = queue.poll();
                        for (int neighbor : adj.get(node)) {
                            if (!visited[neighbor]) { visited[neighbor] = true; queue.add(neighbor); }
                        }
                    }
                }
            }
            """;

    @Test
    void bfsDetector_findsRealBfs() {
        ParsedFile pf = parser.parseOne("Graph.java", BFS_CODE);
        List<Finding> findings = new BfsDetector().detect(pf);
        assertThat(findings).isNotEmpty();
        assertThat(findings.get(0).getAlgorithm()).isEqualTo("BFS");
    }

    // -------------------------------------------------------------------------
    // DFS
    // -------------------------------------------------------------------------

    private static final String DFS_CODE = """
            import java.util.*;
            public class Graph {
                public void dfs(int node, boolean[] visited, List<List<Integer>> adj) {
                    visited[node] = true;
                    for (int neighbor : adj.get(node)) {
                        if (!visited[neighbor]) dfs(neighbor, visited, adj);
                    }
                }
            }
            """;

    @Test
    void dfsDetector_findsRealDfs() {
        ParsedFile pf = parser.parseOne("Graph.java", DFS_CODE);
        List<Finding> findings = new DfsDetector().detect(pf);
        assertThat(findings).isNotEmpty();
        assertThat(findings.get(0).getAlgorithm()).isEqualTo("DFS");
    }

    @Test
    void dfsDetector_ignoresUnrelatedCode() {
        String code = "public class Util { public int max(int a, int b) { return a > b ? a : b; } }";
        ParsedFile pf = parser.parseOne("Util.java", code);
        assertThat(new DfsDetector().detect(pf)).isEmpty();
    }

    // -------------------------------------------------------------------------
    // Dynamic Programming
    // -------------------------------------------------------------------------

    private static final String DP_CODE = """
            import java.util.*;
            public class Fib {
                Map<Integer, Long> memo = new HashMap<>();
                public long fib(int n) {
                    if (n <= 1) return n;
                    if (memo.containsKey(n)) return memo.get(n);
                    long result = fib(n - 1) + fib(n - 2);
                    memo.put(n, result);
                    return result;
                }
            }
            """;

    @Test
    void dpDetector_findsMemoization() {
        ParsedFile pf = parser.parseOne("Fib.java", DP_CODE);
        List<Finding> findings = new DynamicProgrammingDetector().detect(pf);
        assertThat(findings).isNotEmpty();
        assertThat(findings.get(0).getAlgorithm()).contains("Dynamic Programming");
    }

    // -------------------------------------------------------------------------
    // Two Pointers
    // -------------------------------------------------------------------------

    private static final String TWO_POINTER_CODE = """
            public class ArrayUtil {
                public boolean twoPointerSum(int[] arr, int target) {
                    int left = 0, right = arr.length - 1;
                    while (left < right) {
                        int sum = arr[left] + arr[right];
                        if (sum == target) return true;
                        else if (sum < target) left++;
                        else right--;
                    }
                    return false;
                }
            }
            """;

    @Test
    void twoPointerDetector_findsTwoPointer() {
        ParsedFile pf = parser.parseOne("ArrayUtil.java", TWO_POINTER_CODE);
        List<Finding> findings = new TwoPointerDetector().detect(pf);
        assertThat(findings).isNotEmpty();
    }

    // -------------------------------------------------------------------------
    // Data Structure: HashMap
    // -------------------------------------------------------------------------

    private static final String HASHMAP_CODE = """
            import java.util.*;
            public class Counter {
                public Map<String, Integer> count(String[] words) {
                    Map<String, Integer> map = new HashMap<>();
                    for (String w : words) map.merge(w, 1, Integer::sum);
                    return map;
                }
            }
            """;

    @Test
    void dataStructureDetector_findsHashMap() {
        ParsedFile pf = parser.parseOne("Counter.java", HASHMAP_CODE);
        List<Finding> findings = new DataStructureDetector().detect(pf);
        assertThat(findings).anyMatch(f -> f.getAlgorithm().contains("HashMap"));
    }
}
