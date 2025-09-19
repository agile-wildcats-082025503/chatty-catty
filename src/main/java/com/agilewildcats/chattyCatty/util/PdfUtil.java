package com.agilewildcats.chattyCatty.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfUtil {

    public static String extractTextFromPdf(File file) {
        try (PDDocument doc = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse PDF: " + file.getAbsolutePath(), e);
        }
    }
}
