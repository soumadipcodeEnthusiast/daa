package com.race.arena.algorithm.sort;

import com.race.arena.algorithm.AbstractSortAlgorithm;
import com.race.arena.algorithm.TraceRecorder;
import com.race.arena.model.AlgorithmMeta;
import com.race.arena.model.SortRunResult;
import org.springframework.stereotype.Component;

@Component
public class MergeSort extends AbstractSortAlgorithm {

    public static final AlgorithmMeta META = new AlgorithmMeta(
            "merge", "Merge Sort", "comparison",
            "O(n log n)", "O(n log n)", "O(n log n)", "O(n)",
            true, false, true,
            "T(n) = 2T(n/2) + O(n)",
            "Master theorem case 2: a=2, b=2, f(n)=Theta(n) and n^(log_b a) = n^1 = Theta(f(n)), " +
                    "so T(n) = Theta(n log n) in every case, not just on average."
    );

    @Override
    public String id() {
        return "merge";
    }

    @Override
    public String displayName() {
        return "Merge Sort";
    }

    @Override
    public AlgorithmMeta meta() {
        return META;
    }

    @Override
    public SortRunResult runWithTrace(int[] input) {
        return run(input, true, null);
    }

    @Override
    public SortRunResult runFast(int[] input) {
        return run(input, false, null);
    }

    public SortRunResult runWithShadow(int[] input, int[] shadow) {
        return run(input, true, shadow);
    }

    private SortRunResult run(int[] input, boolean trace, int[] shadow) {
        int[] a = input.clone();
        TraceRecorder rec = new TraceRecorder(trace);
        if (shadow != null) rec.attachShadow(shadow);
        rec.addExtraMemory(a.length);
        long start = System.nanoTime();
        if (a.length > 1) {
            int[] aux = new int[a.length];
            int[] shadowAux = shadow != null ? new int[shadow.length] : null;
            mergeSort(a, aux, shadow, shadowAux, 0, a.length - 1, rec);
        }
        for (int i = 0; i < a.length; i++) rec.markSorted(i);
        long elapsed = System.nanoTime() - start;
        return finish(a, rec, elapsed, true, false);
    }

    private void mergeSort(int[] a, int[] aux, int[] shadow, int[] shadowAux, int lo, int hi, TraceRecorder rec) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        mergeSort(a, aux, shadow, shadowAux, lo, mid, rec);
        mergeSort(a, aux, shadow, shadowAux, mid + 1, hi, rec);
        merge(a, aux, shadow, shadowAux, lo, mid, hi, rec);
    }

    private void merge(int[] a, int[] aux, int[] shadow, int[] shadowAux, int lo, int mid, int hi, TraceRecorder rec) {
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
            if (shadow != null) shadowAux[k] = shadow[k];
        }
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                rec.overwrite(a, k, aux[j], shadow != null ? shadowAux[j] : -1);
                j++;
            } else if (j > hi) {
                rec.overwrite(a, k, aux[i], shadow != null ? shadowAux[i] : -1);
                i++;
            } else {
                rec.logCompare(i, j);
                if (aux[i] <= aux[j]) {
                    rec.overwrite(a, k, aux[i], shadow != null ? shadowAux[i] : -1);
                    i++;
                } else {
                    rec.overwrite(a, k, aux[j], shadow != null ? shadowAux[j] : -1);
                    j++;
                }
            }
        }
    }
}
