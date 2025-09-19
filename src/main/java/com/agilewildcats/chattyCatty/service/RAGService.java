package com.agilewildcats.chattyCatty.service;

import com.agilewildcats.chattyCatty.repo.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@Service
public class RAGService {

    private final WebClient webClient;
    private final EmbeddingService embeddingService;
    private final DocumentRepository documentRepository;

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

            return webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class);
        });
    }
}
