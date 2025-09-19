package com.agilewildcats.chattyCatty.util;

import java.util.ArrayList;
import java.util.List;

public class TextChunker {

    /**
     * Splits text into chunks of ~500 tokens (approx. 1500 characters).
     * @param text input text
     * @param chunkSize size in characters
     * @return list of chunks
     */
    public static List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += chunkSize) {
            chunks.add(text.substring(i, Math.min(length, i + chunkSize)));
        }
        return chunks;
    }
}
