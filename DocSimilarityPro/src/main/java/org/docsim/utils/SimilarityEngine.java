package org.docsim.utils;

import org.docsim.algorithms.*;
import org.docsim.model.AlgorithmResult;
import org.docsim.model.Document;
import org.docsim.model.SimilarityResult;

import java.util.*;

/**
 * Orchestrates the full analysis pipeline:
 *   • Runs all 3 similarity algorithms
 *   • Computes weighted final score
 *   • Extracts keywords and common terms
 */
public class SimilarityEngine {

    /** Weights must sum to 1.0 */
    private static final double[] WEIGHTS = {
            0.34,
            0.33,
            0.33
    };

    private static final SimilarityAlgorithm[] ALGORITHMS = {
            new JaccardSimilarity(),
            new RabinKarpSimilarity(),
            new KMPSimilarity()
    };

    /**
     * Run all algorithms and return a populated SimilarityResult.
     */
    public static SimilarityResult analyse(Document doc1, Document doc2) {
        long wallStart = System.currentTimeMillis();

        List<AlgorithmResult> results = new ArrayList<>();
        double weightedSum = 0;

        for (int i = 0; i < ALGORITHMS.length; i++) {
            AlgorithmResult ar = ALGORITHMS[i].run(doc1, doc2);
            results.add(ar);
            weightedSum += ar.getScore() * WEIGHTS[i];
        }

        long totalMs = System.currentTimeMillis() - wallStart;

        SimilarityResult sr = new SimilarityResult();
        sr.setDoc1(doc1);
        sr.setDoc2(doc2);
        sr.setAlgorithmResults(results);
        sr.setFinalScore(Math.min(1.0, weightedSum));
        sr.setTotalTimeMs(totalMs);
        sr.setTopKeywordsDoc1(topKeywords(doc1, 15));
        sr.setTopKeywordsDoc2(topKeywords(doc2, 15));
        sr.setCommonKeywords(commonKeywords(doc1, doc2, 20));
        return sr;
    }

    /** Top-N keywords by frequency. */
    public static List<Map.Entry<String, Integer>> topKeywords(Document doc, int n) {
        if (doc.getTermFrequency() == null) return Collections.emptyList();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(doc.getTermFrequency().entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        return list.subList(0, Math.min(n, list.size()));
    }

    /** Shared keywords present in both documents (by token set intersection). */
    public static List<String> commonKeywords(Document d1, Document d2, int n) {
        if (d1.getTermFrequency() == null || d2.getTermFrequency() == null)
            return Collections.emptyList();
        Set<String> s1 = d1.getTermFrequency().keySet();
        Set<String> s2 = d2.getTermFrequency().keySet();
        List<String> common = new ArrayList<>();
        for (String w : s1) {
            if (s2.contains(w)) common.add(w);
        }
        common.sort((a, b) -> {
            int f1 = d1.getTermFrequency().getOrDefault(a, 0) +
                     d2.getTermFrequency().getOrDefault(a, 0);
            int f2 = d1.getTermFrequency().getOrDefault(b, 0) +
                     d2.getTermFrequency().getOrDefault(b, 0);
            return f2 - f1;
        });
        return common.subList(0, Math.min(n, common.size()));
    }

    public static SimilarityAlgorithm[] getAlgorithms() { return ALGORITHMS; }
    public static double[] getWeights()                 { return WEIGHTS.clone(); }
}
