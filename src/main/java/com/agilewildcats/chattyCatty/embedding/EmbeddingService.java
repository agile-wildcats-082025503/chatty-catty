package com.agilewildcats.chattyCatty.embedding;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class EmbeddingService {
    private final WebClient webClient;

    public EmbeddingService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
                .build();
    }

    public Mono<String> embedText(String text) {
        String body = """
                { "input": "%s", "model": "text-embedding-3-small" }
                """
                .formatted(text);
        return webClient.post()
                .uri("/embeddings")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }
}