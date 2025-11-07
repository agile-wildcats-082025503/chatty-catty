package com.agilewildcats.chattyCatty.controller;

import com.agilewildcats.chattyCatty.service.prompt.PromptProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Entry point for chat queries
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private final PromptProcessor promptProcessor;
    private final static Logger logger = LoggerFactory.getLogger(ChatController.class);

    public ChatController(PromptProcessor promptProcessor) {
        this.promptProcessor = promptProcessor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        ask("Tell me a joke");
    }

    @GetMapping("/general")
    public String ask(@RequestParam(name="message", defaultValue = "Tell me a joke") String message) {
        logger.info("ask : message={}", message);
        return promptProcessor.retrieveAndGenerate(message);
    }

    @GetMapping(value = "/contextual", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> askContextual(@RequestParam(name="q", required = true) String q) {
        logger.info("askContextual : q='{}'", q);
        return promptProcessor.retrieveAndGenerateContextual(q);
    }

}