package com.diff.studio.algolens.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test: POST a real Java file (quicksort) to /api/algolens/analyze
 * and verify the report contains the expected algorithm.
 *
 * The LLM step is skipped because ANTHROPIC_API_KEY is not set in tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AlgoLensIntegrationTest {

    @Autowired
    MockMvc mvc;

    private static final String QUICKSORT_SOURCE = """
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
                        if (arr[j] <= pivot) {
                            i++;
                            int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
                        }
                    }
                    int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
                    return i + 1;
                }
            }
            """;

    @Test
    void analyze_quicksortFile_reportsQuickSort() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "QuickSorter.java", "text/plain", QUICKSORT_SOURCE.getBytes());

        mvc.perform(multipart("/api/algolens/analyze").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.findings").isArray())
                .andExpect(jsonPath("$.findings[?(@.algorithm == 'Quick Sort')]").exists())
                .andExpect(jsonPath("$.projectName").value("QuickSorter"));
    }

    @Test
    void analyze_noInput_returns400() throws Exception {
        mvc.perform(multipart("/api/algolens/analyze"))
                .andExpect(status().isBadRequest());
    }
}
