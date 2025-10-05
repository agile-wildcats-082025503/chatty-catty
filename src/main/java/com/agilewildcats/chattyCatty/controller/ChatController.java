package com.agilewildcats.chattyCatty.controller;

import com.agilewildcats.chattyCatty.service.prompt.PromptProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/general")
    public String ask(@RequestParam(name="message", defaultValue = "Tell me a joke") String message) {
        logger.info("ask : message={}", message);
        return promptProcessor.retrieveAndGenerate(message);
    }

    @GetMapping("/contextual")
    public String askContextual(@RequestParam(name="q", required = true) String q) {
        logger.info("askContextual : q='{}'", q);
        return promptProcessor.retrieveAndGenerateContextual(q);
    }
}