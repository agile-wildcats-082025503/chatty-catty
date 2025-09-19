package com.agilewildcats.chattyCatty.service;

import com.fasterxml.jackson.databind.JsonNode;
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

    public Mono<float[]> embedText(String text) {
        String body = """
    {
      "input": "%s",
      "model": "text-embedding-3-small"
    }
    """.formatted(text);

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

    private float[] parseEmbedding(String json) {
        // TODO: parse JSON into float[] (use Jackson or Gson)
        return new float[1536];
    }
}
