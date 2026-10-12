package com.diff.studio.algolens.model;

import java.util.List;

/**
 * Metadata extracted from a single method declaration.
 * Detectors use these summaries instead of re-walking the AST themselves.
 */
public class MethodInfo {

    private final String name;           // method name
    private final int    startLine;
    private final int    endLine;
    private final String snippet;        // raw source lines startLine..endLine
    private final int    maxLoopDepth;   // deepest nesting of for/while loops
    private final boolean recursive;    // true if the method calls itself
    private final List<String> collectionTypes; // e.g. ["HashMap", "PriorityQueue"]

    public MethodInfo(String name, int startLine, int endLine, String snippet,
                      int maxLoopDepth, boolean recursive, List<String> collectionTypes) {
        this.name             = name;
        this.startLine        = startLine;
        this.endLine          = endLine;
        this.snippet          = snippet;
        this.maxLoopDepth     = maxLoopDepth;
        this.recursive        = recursive;
        this.collectionTypes  = collectionTypes;
    }

    public String       getName()            { return name; }
    public int          getStartLine()       { return startLine; }
    public int          getEndLine()         { return endLine; }
    public String       getSnippet()         { return snippet; }
    public int          getMaxLoopDepth()    { return maxLoopDepth; }
    public boolean      isRecursive()        { return recursive; }
    public List<String> getCollectionTypes() { return collectionTypes; }
}
