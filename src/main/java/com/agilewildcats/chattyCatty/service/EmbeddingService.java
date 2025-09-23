package com.agilewildcats.chattyCatty.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class EmbeddingService {

    private final WebClient webClient;
    private final static Logger logger = LoggerFactory.getLogger(EmbeddingService.class);

    public EmbeddingService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
                .build();
    }

    public Mono<float[]> embedText(String text) {
        String body = """
        {
          "model": "text-embedding-3-small"
          "encoding_format": "float"
          "input": "%s",
        }
        """.formatted(text);

        logger.info("embedText : text={}", text);

        return webClient.post()
                .uri("/embeddings")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)  // Jackson auto-binding
                .map(json -> {
                    var arr = json.get("data").get(0).get("embedding");
                    float[] vector = new float[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        vector[i] = (float) arr.get(i).asDouble();
                    }
                    return vector;
                });
    }
}
