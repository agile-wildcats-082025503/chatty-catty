package com.agilewildcats.chattyCatty.controller;

import com.agilewildcats.chattyCatty.dto.ChatFormattedResponse;
import com.agilewildcats.chattyCatty.service.RAGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.ai.openai.OpenAiChatModel;
import reactor.core.publisher.Flux;

/**
 * Entry point for chat queries
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private OpenAiChatModel chatModel;
    private final RAGService ragService;
    private final static Logger logger = LoggerFactory.getLogger(ChatController.class);

    public ChatController(RAGService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/general")
    public Flux<String> generateMessage(@RequestParam(name="message", defaultValue = "Tell me a joke") String message) {
        logger.info("generateMessage : message={}", message);
        return chatModel.stream(message);
    }

    @GetMapping("/formatted")
    public String askFormatted(@RequestParam(name="q", required = true) String q,
                               @RequestParam(name="format", defaultValue = "markdown") String format) {
        logger.info("askFormatted : format='{}' and q='{}'", format, q);

        ChatFormattedResponse response = ragService.askFormatted(q).block();

        return switch (format.toLowerCase()) {
            case "html" -> response.getHtml();
            case "markdown" -> response.getMarkdown();
            case "plain" -> response.getAnswer();
            default -> response.getMarkdown();
        };
    }
}