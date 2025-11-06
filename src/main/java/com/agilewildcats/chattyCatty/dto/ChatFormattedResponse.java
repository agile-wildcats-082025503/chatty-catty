package com.agilewildcats.chattyCatty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URL;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@AllArgsConstructor
public class ChatFormattedResponse {
    private Map<String, List<RetrievedDoc>> sources;

    @Data
    @AllArgsConstructor
    public static class RetrievedDoc {
        private String sourceName;
        private URL sourceUrl;
        private double similarity;
        @JsonIgnore // Don't serialize the converted file content in chat responses
        private String content;
    }
}