package com.diff.studio.algolens.parser;

import com.diff.studio.algolens.model.MethodInfo;
import com.diff.studio.algolens.model.ParsedFile;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Stage 2 of the AlgoLens pipeline.
 *
 * Parses Java source text into {@link ParsedFile} objects.
 * For each method it extracts:
 *   - max loop nesting depth
 *   - whether the method appears to recurse
 *   - collection types instantiated (HashMap, PriorityQueue, etc.)
 */
@Service
public class ParserService {

    private static final Set<String> COLLECTION_KEYWORDS = Set.of(
            "HashMap", "LinkedHashMap", "TreeMap", "HashSet", "LinkedHashSet", "TreeSet",
            "ArrayList", "LinkedList", "PriorityQueue", "ArrayDeque", "Stack",
            "Deque", "Queue", "Map", "Set", "List"
    );

    private final JavaParser javaParser = new JavaParser();

    /**
     * Parse all collected source files.
     *
     * @param sources map of relative-path → source text (from IngestService)
     * @return list of ParsedFile; files that fail to parse are skipped
     */
    public List<ParsedFile> parseAll(Map<String, String> sources) {
        List<ParsedFile> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            try {
                ParsedFile pf = parseOne(entry.getKey(), entry.getValue());
                result.add(pf);
            } catch (Exception e) {
                // skip files that cannot be parsed
            }
        }
        return result;
    }

    /**
     * Parse a single source text.
     */
    public ParsedFile parseOne(String relativePath, String source) {
        ParseResult<CompilationUnit> result = javaParser.parse(source);
        CompilationUnit cu = result.getResult()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot parse " + relativePath + ": " +
                        result.getProblems().stream()
                              .map(Object::toString)
                              .findFirst().orElse("unknown error")));

        List<MethodInfo> methods = extractMethods(cu, source, relativePath);
        return new ParsedFile(relativePath, source, cu, methods);
    }

    // -------------------------------------------------------------------------

    private List<MethodInfo> extractMethods(CompilationUnit cu, String source, String filePath) {
        String[] lines = source.split("\n", -1);
        List<MethodInfo> methods = new ArrayList<>();

        cu.findAll(MethodDeclaration.class).forEach(md -> {
            try {
                String name      = md.getNameAsString();
                int    startLine = md.getBegin().map(p -> p.line).orElse(0);
                int    endLine   = md.getEnd().map(p -> p.line).orElse(startLine);

                String snippet = extractLines(lines, startLine, endLine);
                int    loopDepth = maxLoopDepth(md.getBody().orElse(null));
                boolean recursive = isRecursive(md);
                List<String> collections = collectionsUsed(md);

                methods.add(new MethodInfo(name, startLine, endLine, snippet,
                        loopDepth, recursive, collections));
            } catch (Exception e) {
                // skip malformed method
            }
        });
        return methods;
    }

    /**
     * Compute the deepest loop nesting inside a block statement.
     */
    static int maxLoopDepth(Statement body) {
        if (body == null) return 0;
        return loopDepth(body, 0);
    }

    private static int loopDepth(Statement stmt, int current) {
        int depth = current;

        if (stmt instanceof ForStmt || stmt instanceof ForEachStmt
                || stmt instanceof WhileStmt || stmt instanceof DoStmt) {
            current++;
            depth = current;
        }

        // Recurse into children
        for (Statement child : stmt.findAll(Statement.class)) {
            if (child == stmt) continue;
            // only direct children to avoid double-counting
        }

        // Simple approach: walk ALL nested loops, track max
        List<Statement> loops = new ArrayList<>();
        collectLoops(stmt, loops);
        return computeMaxDepth(stmt, 0);
    }

    private static int computeMaxDepth(Statement stmt, int currentDepth) {
        boolean isLoop = (stmt instanceof ForStmt || stmt instanceof ForEachStmt
                || stmt instanceof WhileStmt || stmt instanceof DoStmt);

        int depth = isLoop ? currentDepth + 1 : currentDepth;
        int maxChild = depth;

        // Get direct child statements
        for (com.github.javaparser.ast.Node child : stmt.getChildNodes()) {
            if (child instanceof Statement childStmt) {
                int childDepth = computeMaxDepth(childStmt, depth);
                maxChild = Math.max(maxChild, childDepth);
            }
            if (child instanceof com.github.javaparser.ast.body.MethodDeclaration) {
                // don't descend into nested method/lambda for loop depth
            }
        }
        return maxChild;
    }

    private static void collectLoops(Statement stmt, List<Statement> out) {
        if (stmt instanceof ForStmt || stmt instanceof ForEachStmt
                || stmt instanceof WhileStmt || stmt instanceof DoStmt) {
            out.add(stmt);
        }
        stmt.getChildNodes().stream()
                .filter(n -> n instanceof Statement)
                .map(n -> (Statement) n)
                .forEach(s -> collectLoops(s, out));
    }

    /**
     * True if the method contains a call to itself (direct recursion).
     */
    private static boolean isRecursive(MethodDeclaration md) {
        String name = md.getNameAsString();
        return md.findAll(MethodCallExpr.class).stream()
                .anyMatch(call -> call.getNameAsString().equals(name));
    }

    /**
     * Collection type names instantiated or referenced inside the method.
     */
    private static List<String> collectionsUsed(MethodDeclaration md) {
        Set<String> found = new LinkedHashSet<>();
        md.findAll(ObjectCreationExpr.class).forEach(expr -> {
            String type = expr.getType().getNameAsString();
            if (COLLECTION_KEYWORDS.contains(type)) {
                found.add(type);
            }
        });
        return new ArrayList<>(found);
    }

    private static String extractLines(String[] lines, int startLine, int endLine) {
        int from = Math.max(0, startLine - 1);
        int to   = Math.min(lines.length, endLine);
        return String.join("\n", Arrays.copyOfRange(lines, from, to));
    }
}
