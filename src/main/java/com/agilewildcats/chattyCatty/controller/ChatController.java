package com.agilewildcats.chattyCatty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.ai.openai.OpenAiChatModel;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Autowired
    private OpenAiChatModel chatModel;

    @GetMapping("/ai/chat/string")
    public Flux<String> generateString(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return chatModel.stream(message);
    }
}