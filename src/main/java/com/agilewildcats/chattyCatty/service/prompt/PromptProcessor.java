package com.agilewildcats.chattyCatty.service.prompt;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.agilewildcats.chattyCatty.dto.ChatFormattedResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.commonmark.node.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PromptProcessor {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static Logger logger = LoggerFactory.getLogger(PromptProcessor.class);

    // Add more of these as you add more specific prompts.
    // TODO: Instead, just scan the /prompts/ directory and attach resources dynamically. Then pick a prompt that applies to the message best.
    @Value("classpath:/prompts/prompt_for_info.st")
    private Resource promptForInfo;

    public PromptProcessor(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    /**
     * Retrieves a response from the AI without using context from the DB
     * @param message Message to send to the AI
     * @return AI response
     */
    public String retrieveAndGenerate(String message) {
        // 1. Retrieve similar documents
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.builder().query(message).topK(4).build());
        String information = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));
        // TODO: Add context as Source: source

        // 2. Augment the prompt
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(promptForInfo);
        Prompt prompt = new Prompt(List.of(
                systemPromptTemplate.createMessage(Map.of("information", information)),
                new UserMessage(message)));

        // 3. Generate the response
        return chatClient.prompt(prompt).call().content();
    }

    /**
     * Retrieves an AI response using the context info that has been loaded from the DB
     * @param message Message to send to the AI
     * @return AI response and contextual data
     */
    public Flux<String> retrieveAndGenerateContextual(String message) {
        logger.info("retrieveAndGenerateContextual : message='{}'", message);

        // 1. Retrieve similar documents
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(4)
                        .build()
        );

        Map<String, List<ChatFormattedResponse.RetrievedDoc>> groupedDocs =
                results.stream()
                        .map(r -> {
                            String content = (String) r.getText();
                            String sourceName = (String) r.getMetadata().get("sourceName");
                            Double distance = r.getScore();
                            double similarity = 1 - (distance != null ? distance : 1);
                            return Map.entry(sourceName,
                                    new ChatFormattedResponse.RetrievedDoc(sourceName, null, similarity, content));
                        })
                        .collect(Collectors.groupingBy(Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        StringBuilder contextBuilder = new StringBuilder();
        groupedDocs.forEach((source, docs) -> {
            contextBuilder.append("Source: ").append(source).append("\n");
            docs.forEach(doc -> contextBuilder.append(doc.getContent()).append("\n"));
            contextBuilder.append("\n");
        });
        ChatFormattedResponse response = new ChatFormattedResponse(groupedDocs);

        // 2. Augment the prompt
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(promptForInfo);
        Prompt prompt = new Prompt(List.of(
                systemPromptTemplate.createMessage(Map.of("information", contextBuilder.toString())),
                new UserMessage(message)));

        // 3. Generate the response
        Flux<String> aiStream = chatClient.prompt(prompt).stream().content().transform(flux -> toChunk(flux, 100));
        try {
            return aiStream.concatWith(
                    Flux.just(objectMapper.writeValueAsString(response))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Flux<String> toChunk(Flux<String> tokenFlux, int chunkSize) {
        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();
            tokenFlux.subscribe(
                    token -> {
                        buffer.append(token);
                        if (buffer.length() >= chunkSize) {
                            sink.next("{{{" + buffer.toString() + "}}}");
                            buffer.setLength(0);
                        }
                    },
                    sink::error,
                    () -> {
                        if (buffer.length() > 0) {
                            sink.next(buffer.toString());
                        }
                        sink.complete();
                    }
            );
        });
    }
}
