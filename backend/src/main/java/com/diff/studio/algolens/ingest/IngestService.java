package com.diff.studio.algolens.ingest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Stage 1 of the AlgoLens pipeline.
 *
 * Accepts:
 *   - A single .java file upload
 *   - A .zip upload containing Java sources
 *   - A public GitHub HTTPS URL (shallow clone, depth 1)
 *
 * Returns a map of relative-path → source-text for every .java file found
 * (excluding test folders and build output).
 *
 * Security rules enforced:
 *   - Only https:// GitHub URLs accepted
 *   - Upload size checked (max 5 MB raw, max 3 MB per zip entry)
 *   - Zip path traversal rejected
 *   - Temp directories cleaned up after use
 */
@Service
public class IngestService {

    /** Max total upload size: 5 MB */
    private static final long MAX_UPLOAD_BYTES = 5 * 1024 * 1024L;

    /** Max size of a single file extracted from a zip: 3 MB */
    private static final long MAX_ENTRY_BYTES = 3 * 1024 * 1024L;

    /** Directories to skip */
    private static final Set<String> SKIP_DIRS = Set.of(
            "test", "tests", "target", "build", ".git", "node_modules", "__pycache__"
    );

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Ingest a multipart upload (either a .java file or a .zip archive).
     */
    public Map<String, String> ingestUpload(MultipartFile file) throws IOException {
        String name = Objects.requireNonNullElse(file.getOriginalFilename(), "upload");
        long   size = file.getSize();

        if (size > MAX_UPLOAD_BYTES) {
            throw new IllegalArgumentException(
                    "Upload too large: " + size + " bytes. Maximum is " + MAX_UPLOAD_BYTES + " bytes.");
        }

        if (name.toLowerCase().endsWith(".java")) {
            String source = new String(file.getBytes(), StandardCharsets.UTF_8);
            return Map.of(name, source);
        }

        if (name.toLowerCase().endsWith(".zip")) {
            return extractZip(file.getInputStream());
        }

        throw new IllegalArgumentException(
                "Unsupported file type. Upload a .java file or a .zip archive.");
    }

    /**
     * Ingest a public GitHub repository URL.
     * The URL must start with https://github.com/.
     */
    public Map<String, String> ingestGitHub(String url) throws IOException, GitAPIException {
        validateGitHubUrl(url);

        Path tempDir = Files.createTempDirectory("algolens-repo-");
        try {
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(tempDir.toFile())
                    .setDepth(1)
                    .setNoCheckout(false)
                    .call()
                    .close();

            return collectJavaFiles(tempDir, tempDir);
        } finally {
            FileUtils.deleteDirectory(tempDir.toFile());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void validateGitHubUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("GitHub URL must not be blank.");
        }
        try {
            URI uri = URI.create(url);
            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException(
                        "Only https:// GitHub URLs are accepted. Got: " + url);
            }
            String host = uri.getHost();
            if (host == null || !host.equalsIgnoreCase("github.com")) {
                throw new IllegalArgumentException(
                        "Only github.com URLs are accepted. Got: " + url);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }

    private Map<String, String> extractZip(InputStream in) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        try (ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();

                // Reject path traversal
                if (name.contains("..") || name.startsWith("/")) {
                    throw new IllegalArgumentException(
                            "Zip entry with suspicious path rejected: " + name);
                }

                if (entry.isDirectory() || !name.toLowerCase().endsWith(".java")) {
                    zis.closeEntry();
                    continue;
                }

                if (shouldSkip(name)) {
                    zis.closeEntry();
                    continue;
                }

                byte[] bytes = readLimited(zis, MAX_ENTRY_BYTES, name);
                result.put(name, new String(bytes, StandardCharsets.UTF_8));
                zis.closeEntry();
            }
        }
        return result;
    }

    private Map<String, String> collectJavaFiles(Path root, Path base) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        try (var stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                  .filter(p -> p.toString().endsWith(".java"))
                  .filter(p -> !shouldSkip(base.relativize(p).toString()))
                  .sorted()
                  .forEach(p -> {
                      try {
                          String rel     = base.relativize(p).toString().replace('\\', '/');
                          String source  = Files.readString(p, StandardCharsets.UTF_8);
                          result.put(rel, source);
                      } catch (IOException e) {
                          // skip unreadable files silently
                      }
                  });
        }
        return result;
    }

    private boolean shouldSkip(String relativePath) {
        String[] parts = relativePath.replace('\\', '/').split("/");
        for (String part : parts) {
            if (SKIP_DIRS.contains(part.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private byte[] readLimited(InputStream in, long maxBytes, String entryName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        long total = 0;
        int n;
        while ((n = in.read(buf)) != -1) {
            total += n;
            if (total > maxBytes) {
                throw new IllegalArgumentException(
                        "Zip entry too large: " + entryName + ". Maximum is " + maxBytes + " bytes.");
            }
            baos.write(buf, 0, n);
        }
        return baos.toByteArray();
    }
}
