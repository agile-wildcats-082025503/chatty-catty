package com.agilewildcats.chattyCatty.controller;

import com.agilewildcats.chattyCatty.dto.ChatFormattedResponse;
import com.agilewildcats.chattyCatty.service.RAGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.ai.openai.OpenAiChatModel;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final RAGService ragService;

    public ChatController(RAGService ragService) {
        this.ragService = ragService;
    }

    @Autowired
    private OpenAiChatModel chatModel;

    @GetMapping("/general")
    public Flux<String> generateString(@RequestParam(name="message", defaultValue = "Tell me a joke") String message) {
        return chatModel.stream(message);
    }

    @GetMapping("/formatted")
    public String askFormatted(@RequestParam(name="q", required = true) String q,
                               @RequestParam(name="format", defaultValue = "markdown") String format) {
        ChatFormattedResponse response = ragService.askFormatted(q).block();

        return switch (format.toLowerCase()) {
            case "html" -> response.getHtml();
            case "markdown" -> response.getMarkdown();
            case "plain" -> response.getAnswer();
            default -> response.getMarkdown();
        };
    }
}