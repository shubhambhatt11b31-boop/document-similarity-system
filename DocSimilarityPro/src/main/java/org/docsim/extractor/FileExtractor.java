package org.docsim.extractor;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.docsim.model.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Extracts raw text from TXT, PDF, and DOCX files.
 * Also populates metadata on the Document object.
 */
public class FileExtractor {

    /**
     * Load a file into a new Document, extracting raw text and metadata.
     *
     * @param file  File to load (TXT / PDF / DOCX)
     * @return populated Document with rawText set
     * @throws IOException if file cannot be read
     */
    public static Document load(File file) throws IOException {
        String name = file.getName();
        String ext  = extension(name).toLowerCase();

        Document doc = new Document(file.getAbsolutePath(), name);
        doc.setFileSizeBytes(file.length());
        doc.setFileType(ext.toUpperCase());

        String text = switch (ext) {
            case "pdf"  -> extractPDF(file);
            case "docx" -> extractDOCX(file);
            default     -> extractTXT(file);  // txt, md, csv, etc.
        };

        doc.setRawText(text);
        return doc;
    }

    // ── Extractors ──────────────────────────────────────────

    private static String extractTXT(File file) throws IOException {
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

    private static String extractPDF(File file) throws IOException {
        try (PDDocument pdf = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdf);
        }
    }

    private static String extractDOCX(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument docx   = new XWPFDocument(fis)) {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph p : docx.getParagraphs()) {
                sb.append(p.getText()).append(' ');
            }
            return sb.toString();
        }
    }

    // ── Helpers ─────────────────────────────────────────────

    /** Extract file extension (without the dot). */
    public static String extension(String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : "";
    }

    /** Check if the extension is supported. */
    public static boolean isSupported(File f) {
        String ext = extension(f.getName()).toLowerCase();
        return ext.equals("txt") || ext.equals("pdf") || ext.equals("docx");
    }
}
