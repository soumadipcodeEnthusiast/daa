package com.diff.studio.algolens.model;

import com.github.javaparser.ast.CompilationUnit;
import java.util.List;

/**
 * Result of parsing one Java source file.
 * Carries the raw source, the JavaParser AST, and metadata used by detectors.
 */
public class ParsedFile {

    private final String            relativePath; // e.g. "src/main/java/Foo.java"
    private final String            source;       // full file text
    private final CompilationUnit   ast;
    private final List<MethodInfo>  methods;

    public ParsedFile(String relativePath, String source, CompilationUnit ast, List<MethodInfo> methods) {
        this.relativePath = relativePath;
        this.source       = source;
        this.ast          = ast;
        this.methods      = methods;
    }

    public String           getRelativePath() { return relativePath; }
    public String           getSource()       { return source; }
    public CompilationUnit  getAst()          { return ast; }
    public List<MethodInfo> getMethods()      { return methods; }
}
