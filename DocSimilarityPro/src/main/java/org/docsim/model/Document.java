package org.docsim.model;

import java.util.List;
import java.util.Map;

/**
 * Holds the raw text, cleaned text, token list, and term-frequency
 * map for a single uploaded document.
 */
public class Document {

    private final String filePath;
    private final String fileName;
    private String rawText;
    private String cleanText;
    private List<String> tokens;
    private Map<String, Integer> termFrequency;   // word → count
    private long fileSizeBytes;
    private String fileType;   // TXT / PDF / DOCX

    public Document(String filePath, String fileName) {
        this.filePath  = filePath;
        this.fileName  = fileName;
    }

    // ── Getters / setters ────────────────────────────────────
    public String getFilePath()  { return filePath; }
    public String getFileName()  { return fileName; }

    public String getRawText()   { return rawText; }
    public void   setRawText(String t) { this.rawText = t; }

    public String getCleanText() { return cleanText; }
    public void   setCleanText(String t) { this.cleanText = t; }

    public List<String> getTokens() { return tokens; }
    public void setTokens(List<String> tokens) { this.tokens = tokens; }

    public Map<String, Integer> getTermFrequency() { return termFrequency; }
    public void setTermFrequency(Map<String, Integer> tf) { this.termFrequency = tf; }

    public long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(long s) { this.fileSizeBytes = s; }

    public String getFileType() { return fileType; }
    public void setFileType(String t) { this.fileType = t; }

    /** Human-readable file size. */
    public String getFileSizeFormatted() {
        if (fileSizeBytes < 1024) return fileSizeBytes + " B";
        if (fileSizeBytes < 1024*1024) return String.format("%.1f KB", fileSizeBytes/1024.0);
        return String.format("%.2f MB", fileSizeBytes/(1024.0*1024));
    }

    public int getWordCount() {
        return tokens == null ? 0 : tokens.size();
    }

    public int getUniqueWordCount() {
        return termFrequency == null ? 0 : termFrequency.size();
    }

    @Override
    public String toString() {
        return fileName + " (" + fileType + ", " + getFileSizeFormatted() + ")";
    }
}
