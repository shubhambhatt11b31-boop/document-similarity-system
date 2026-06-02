package org.docsim.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


public class SimilarityResult {

    private Document doc1;
    private Document doc2;

    private List<AlgorithmResult> algorithmResults;


    private double finalScore;
    private long   totalTimeMs;

    // Top keywords from each document
    private List<Map.Entry<String, Integer>> topKeywordsDoc1;
    private List<Map.Entry<String, Integer>> topKeywordsDoc2;
    private List<String> commonKeywords;

    // When analysis was run
    private final String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    // ── Getters / setters ────────────────────────────────────
    public Document  getDoc1()          { return doc1; }
    public void      setDoc1(Document d){ this.doc1 = d; }

    public Document  getDoc2()          { return doc2; }
    public void      setDoc2(Document d){ this.doc2 = d; }

    public List<AlgorithmResult> getAlgorithmResults() { return algorithmResults; }
    public void setAlgorithmResults(List<AlgorithmResult> r) { this.algorithmResults = r; }

    public double getFinalScore()          { return finalScore; }
    public void   setFinalScore(double s)  { this.finalScore = s; }

    public long   getTotalTimeMs()         { return totalTimeMs; }
    public void   setTotalTimeMs(long t)   { this.totalTimeMs = t; }

    public List<Map.Entry<String,Integer>> getTopKeywordsDoc1() { return topKeywordsDoc1; }
    public void setTopKeywordsDoc1(List<Map.Entry<String,Integer>> k) { this.topKeywordsDoc1 = k; }

    public List<Map.Entry<String,Integer>> getTopKeywordsDoc2() { return topKeywordsDoc2; }
    public void setTopKeywordsDoc2(List<Map.Entry<String,Integer>> k) { this.topKeywordsDoc2 = k; }

    public List<String> getCommonKeywords()             { return commonKeywords; }
    public void setCommonKeywords(List<String> k)       { this.commonKeywords = k; }

    public String getTimestamp()  { return timestamp; }

    /** Returns "Highly Similar", "Moderately Similar", etc. */
    public String getVerdict() {
        double pct = finalScore * 100;
        if (pct >= 80) return "Highly Similar";
        if (pct >= 60) return "Moderately Similar";
        if (pct >= 40) return "Somewhat Similar";
        if (pct >= 20) return "Slightly Similar";
        return "Very Different";
    }

    public String getFinalScorePercent() {
        return String.format("%.2f%%", finalScore * 100);
    }
}
