package com.diff.studio.algolens.llm;

import com.diff.studio.algolens.model.Finding;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/**
 * Stage 5: LLM explanation step using the Anthropic Messages API.
 *
 * For each Finding this service sends the code snippet + evidence to the LLM
 * and asks it to:
 *   1. Confirm or reject the detection
 *   2. Write 2-3 plain sentences explaining what it sees
 *
 * Rules:
 *   - If ANTHROPIC_API_KEY is not set, the step is skipped silently.
 *   - LLM results are cached in Caffeine, keyed by a SHA-256 of the snippet.
 *   - A single retry is attempted on HTTP 429 or 5xx.
 *   - Only the first {@code algolens.llm.max-snippets} findings are sent.
 */
@Service
public class LlmExplainService {

    private static final Logger log = LoggerFactory.getLogger(LlmExplainService.class);

    private static final String ANTHROPIC_API_URL  = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION  = "2023-06-01";
    private static final String MODEL              = "claude-3-5-haiku-20241022";
    private static final int    MAX_TOKENS_REPLY   = 300;

    private static final String SYSTEM_PROMPT = """
            You are a code analysis tool. Your job is to read a Java code snippet and decide whether
            a specific algorithm or data structure is present.

            Rules for your response:
            - Short sentences. Simple words.
            - No praise, no hype, no exclamation marks.
            - Separate what is in the code (fact) from what you infer (guess).
            - If you are unsure, say so.
            - Respond with valid JSON only, using this exact structure:
              {
                "confirmed": true or false,
                "explanation": "2-3 plain sentences"
              }
            """;

    @Value("${ANTHROPIC_API_KEY:}")
    private String apiKey;

    @Value("${algolens.llm.max-snippets:20}")
    private int maxSnippets;

    private final RestClient   restClient;
    private final ObjectMapper objectMapper;

    public LlmExplainService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient   = RestClient.builder()
                .baseUrl(ANTHROPIC_API_URL)
                .build();
    }

    /**
     * Annotate findings with LLM confirmations.
     * If the API key is absent, this method returns without modifying any finding.
     */
    public void explain(List<Finding> findings) {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("ANTHROPIC_API_KEY not set — skipping LLM explanation step.");
            return;
        }

        int count = 0;
        for (Finding f : findings) {
            if (count >= maxSnippets) break;
            try {
                explainOne(f);
            } catch (Exception e) {
                log.warn("LLM call failed for '{}' in {}: {}", f.getAlgorithm(), f.getFile(), e.getMessage());
            }
            count++;
        }
    }

    @Cacheable(value = "llmResults", key = "#snippetHash")
    public LlmResult callWithCache(String snippetHash, String prompt) {
        return callAnthropicWithRetry(prompt);
    }

    /**
     * Call the LLM for a single Finding. Results are cached by snippet hash.
     */
    private void explainOne(Finding finding) throws Exception {
        String snippet = finding.getMethod().isBlank()
                ? "(whole file)"
                : finding.getMethod() + " — lines " + finding.getStartLine() + "–" + finding.getEndLine();

        String prompt = "Algorithm suspected: " + finding.getAlgorithm() + "\n" +
                        "Evidence from rule-based detector: " + finding.getEvidence() + "\n" +
                        "Location: " + finding.getFile() + ", method: " + snippet + "\n\n" +
                        "Please confirm or reject this detection and explain in 2-3 sentences.";

        String hash = sha256(prompt);
        LlmResult result = callWithCache(hash, prompt);

        finding.setLlmExplanation(result.explanation());
        finding.setLlmConfirmed(result.confirmed());
        if (!result.confirmed()) {
            finding.setAdjustedConfidence(finding.getConfidence() * 0.4);
        }
    }

    private LlmResult callAnthropicWithRetry(String userMessage) {
        try {
            return callAnthropic(userMessage);
        } catch (LlmCallException e) {
            if (e.status == 429 || e.status >= 500) {
                log.info("LLM returned {}; retrying once.", e.status);
                try { Thread.sleep(1500); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
                return callAnthropic(userMessage);
            }
            throw e;
        }
    }

    private LlmResult callAnthropic(String userMessage) {
        Map<String, Object> body = Map.of(
                "model",      MODEL,
                "max_tokens", MAX_TOKENS_REPLY,
                "system",     SYSTEM_PROMPT,
                "messages",   List.of(Map.of("role", "user", "content", userMessage))
        );

        try {
            String responseBody = restClient.post()
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", ANTHROPIC_VERSION)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (req, res) -> {
                                throw new LlmCallException(res.getStatusCode().value(),
                                        "Anthropic API error: " + res.getStatusCode());
                            })
                    .body(String.class);

            AnthropicResponse ar = objectMapper.readValue(responseBody, AnthropicResponse.class);
            String text = ar.content() != null && !ar.content().isEmpty()
                    ? ar.content().get(0).text()
                    : "{}";

            // Strip possible markdown fences
            text = text.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```\\s*", "").trim();

            LlmResponseJson parsed = objectMapper.readValue(text, LlmResponseJson.class);
            return new LlmResult(
                    Boolean.TRUE.equals(parsed.confirmed()),
                    parsed.explanation() != null ? parsed.explanation() : "No explanation provided.");

        } catch (LlmCallException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Failed to parse LLM response: {}", e.getMessage());
            return new LlmResult(true, "LLM response could not be parsed.");
        }
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return String.valueOf(input.hashCode());
        }
    }

    // ---- Inner types ----

    public record LlmResult(boolean confirmed, String explanation) {}

    static class LlmCallException extends RuntimeException {
        final int status;
        LlmCallException(int status, String msg) { super(msg); this.status = status; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AnthropicResponse(List<ContentBlock> content) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ContentBlock(String type, String text) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record LlmResponseJson(Boolean confirmed, String explanation) {}
}
