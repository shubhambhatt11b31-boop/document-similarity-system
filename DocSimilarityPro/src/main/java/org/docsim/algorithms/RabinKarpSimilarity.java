package org.docsim.algorithms;

import org.docsim.model.Document;

import java.util.HashSet;
import java.util.Set;


public class RabinKarpSimilarity implements SimilarityAlgorithm {

    private static final int  N    = 3;              // shingle size (trigrams)
    private static final long BASE = 31L;
    private static final long MOD  = 1_000_000_007L;

    @Override
    public double calculate(Document d1, Document d2) {
        Set<Long> h1 = shingleHashes(d1);
        Set<Long> h2 = shingleHashes(d2);

        long matches = h2.stream().filter(h1::contains).count();

        int total = Math.max(h1.size(), h2.size());
        return total == 0 ? 0.0 : (double) matches / total;
    }


    private Set<Long> shingleHashes(Document doc) {
        var tokens = doc.getTokens();
        Set<Long> hashes = new HashSet<>();

        if (tokens.size() < N) {
            // If fewer tokens than shingle size, hash the whole list as one shingle
            hashes.add(hashString(String.join(" ", tokens)));
            return hashes;
        }

        // Pre-compute BASE^(shingleLen-1) mod MOD for rolling update
        for (int i = 0; i <= tokens.size() - N; i++) {
            String shingle = String.join(" ", tokens.subList(i, i + N));
            hashes.add(hashString(shingle));
        }
        return hashes;
    }


    private long hashString(String s) {
        long hash = 0, pow = 1;
        for (char c : s.toCharArray()) {
            hash = (hash + c * pow) % MOD;
            pow  = (pow * BASE) % MOD;
        }
        return hash;
    }

    @Override public String getName()           { return "Rabin-Karp"; }
    @Override public String getTimeComplexity() { return "O(n + m)"; }
    @Override public String getSpaceComplexity(){ return "O(n + m)"; }
    @Override public String getDescription()    {
        return "Rolling-hash on trigram shingles. Excellent for detecting copied passages.";
    }
}
