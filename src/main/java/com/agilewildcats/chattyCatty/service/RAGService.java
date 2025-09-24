package com.agilewildcats.chattyCatty.service;

import com.agilewildcats.chattyCatty.dto.ChatFormattedResponse;
import com.agilewildcats.chattyCatty.repo.DocumentRepository;
import com.agilewildcats.chattyCatty.util.EmbeddingUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OpenAI chat tool using Retrieval-Augmented Generation to provide specific context
 * for AI queries. Uses a document repository to fetch information to add to the
 * query that provides the AI with contextual information.
 */
@Service
public class RAGService {

    private final WebClient webClient;
    private final EmbeddingService embeddingService;
    private final DocumentRepository documentRepository;
    private final static Logger logger = LoggerFactory.getLogger(RAGService.class);

    public RAGService(WebClient.Builder builder, EmbeddingService embeddingService, DocumentRepository repo) {
        this.webClient = builder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
                .build();
        this.embeddingService = embeddingService;
        this.documentRepository = repo;
    }

    public Mono<String> ask(String query) {
        return embeddingService.embedText(query).flatMap(embedding -> {
            var docs = documentRepository.findRelevant(embedding);
            String context = docs.stream().map(d -> d.getContent()).reduce("", (a, b) -> a + "\n" + b);

            String body = """
            {
              "model": "gpt-4o-mini",
              "messages": [
                {"role": "system", "content": "Answer using the following context:"},
                {"role": "system", "content": "%s"},
                {"role": "user", "content": "%s"}
              ]
            }
            """.formatted(context, query);

            logger.debug("ask : query={}", query);

            return webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class);
        });
    }
    public Mono<ChatFormattedResponse> askFormatted(String query) {
        return embeddingService.embedText(query).flatMap(embedding -> {
            String embeddingStr = EmbeddingUtils.toJson(embedding);

            List<Object[]> results = documentRepository.findTopKWithScores(embeddingStr, 5);

            logger.debug("askFormatted : query={}", query);

            double threshold = 0.80;
            Map<String, List<ChatFormattedResponse.RetrievedDoc>> groupedDocs =
                    results.stream()
                            .map(r -> {
                                String content = (String) r[1];
                                String source = (String) r[2];
                                double distance = (double) r[3];
                                double similarity = 1 - distance;
                                return Map.entry(source,
                                        new ChatFormattedResponse.RetrievedDoc(content, similarity));
                            })
                            .filter(entry -> entry.getValue().getSimilarity() >= threshold)
                            .collect(Collectors.groupingBy(Map.Entry::getKey,
                                    Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

            StringBuilder contextBuilder = new StringBuilder();
            groupedDocs.forEach((source, docs) -> {
                contextBuilder.append("Source: ").append(source).append("\n");
                docs.forEach(doc -> contextBuilder.append(doc.getContent()).append("\n"));
                contextBuilder.append("\n");
            });

            String body = """
            {
              "model": "gpt-4o-mini",
              "messages": [
                {"role": "system", "content": "Answer using only the following context. Cite sources in parentheses by filename, e.g., (Spring Boot Docs)."},
                {"role": "system", "content": "%s"},
                {"role": "user", "content": "%s"}
              ]
            }
            """.formatted(contextBuilder.toString(), query);

            return webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(json -> {
                        String answer = json.get("choices").get(0).get("message").get("content").asText();

                        // Build Markdown
                        StringBuilder md = new StringBuilder();
                        md.append("### Answer\n\n").append(answer).append("\n\n");
                        md.append("<details>\n<summary>📚 Sources</summary>\n\n");
                        groupedDocs.forEach((source, docs) -> {
                            md.append("**").append(source).append("**\n");
                            for (ChatFormattedResponse.RetrievedDoc doc : docs) {
                                md.append("- ").append(doc.getContent())
                                        .append(" _(similarity: ")
                                        .append(String.format("%.2f", doc.getSimilarity()))
                                        .append(")_\n");
                            }
                            md.append("\n");
                        });
                        md.append("</details>");

                        String markdown = md.toString();

                        // Convert Markdown → HTML
                        Parser parser = Parser.builder().build();
                        Node document = parser.parse(markdown);
                        HtmlRenderer renderer = HtmlRenderer.builder().build();
                        String html = renderer.render(document);

                        return new ChatFormattedResponse(answer, markdown, html, groupedDocs);
                    });
        });
    }
}
