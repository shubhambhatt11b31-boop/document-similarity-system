package org.docsim.algorithms;

import org.docsim.model.Document;

import java.util.List;


public class KMPSimilarity implements SimilarityAlgorithm {

    private static final int SHINGLE = 2;   // bigrams

    @Override
    public double calculate(Document d1, Document d2) {
        List<String> t1 = d1.getTokens();
        List<String> t2 = d2.getTokens();

        // Use shorter doc as "pattern", longer as "text"
        List<String> text    = t1.size() >= t2.size() ? t1 : t2;
        List<String> pattern = t1.size() <  t2.size() ? t1 : t2;

        if (pattern.size() < SHINGLE || text.isEmpty()) return 0.0;

        int matched = 0;
        int total   = pattern.size() - SHINGLE + 1;

        for (int i = 0; i <= pattern.size() - SHINGLE; i++) {
            List<String> shingle = pattern.subList(i, i + SHINGLE);
            if (kmpSearch(text, shingle)) matched++;
        }

        return total == 0 ? 0.0 : (double) matched / total;
    }


    private boolean kmpSearch(List<String> text, List<String> pat) {
        int n = text.size();
        int m = pat.size();
        int[] lps = buildLPS(pat);

        int i = 0, j = 0;
        while (i < n) {
            if (text.get(i).equals(pat.get(j))) {
                i++; j++;
                if (j == m) return true;  // found
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return false;
    }

    /** Build the Longest Proper Prefix-Suffix (failure function) array. */
    private int[] buildLPS(List<String> pat) {
        int m   = pat.size();
        int[] lps = new int[m];
        int len = 0, i = 1;
        while (i < m) {
            if (pat.get(i).equals(pat.get(len))) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    @Override public String getName()           { return "KMP"; }
    @Override public String getTimeComplexity() { return "O(n + m)"; }
    @Override public String getSpaceComplexity(){ return "O(m)"; }
    @Override public String getDescription()    {
        return "Knuth-Morris-Pratt on bigram shingles. Efficient exact pattern matching.";
    }
}
