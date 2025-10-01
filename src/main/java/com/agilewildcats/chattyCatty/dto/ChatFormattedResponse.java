package com.agilewildcats.chattyCatty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ChatFormattedResponse {
    private String answer;      // plain text answer
    private Map<String, List<RetrievedDoc>> sources;

    @Data
    @AllArgsConstructor
    public static class RetrievedDoc {
        private String content;
        private String sourceName;
        private double similarity;
    }
}