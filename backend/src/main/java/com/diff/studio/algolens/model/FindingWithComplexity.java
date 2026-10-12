package com.diff.studio.algolens.model;

/**
 * A Finding decorated with its estimated Big-O complexity label.
 */
public class FindingWithComplexity {

    private final Finding finding;
    private final String  timeComplexity;  // e.g. "O(n^2) estimated"

    public FindingWithComplexity(Finding finding, String timeComplexity) {
        this.finding        = finding;
        this.timeComplexity = timeComplexity;
    }

    public Finding getFinding()        { return finding; }
    public String  getTimeComplexity() { return timeComplexity; }
}
