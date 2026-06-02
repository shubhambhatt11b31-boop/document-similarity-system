package org.docsim.algorithms;

import org.docsim.model.Document;

import java.util.HashSet;
import java.util.Set;
public class JaccardSimilarity implements SimilarityAlgorithm {

    @Override
    public double calculate(Document d1, Document d2) {
        Set<String> set1 = new HashSet<>(d1.getTokens());
        Set<String> set2 = new HashSet<>(d2.getTokens());

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    @Override public String getName()           {
        return "Jaccard Similarity";
    }
    @Override public String getTimeComplexity() { return "O(n + m)"; }
    @Override public String getSpaceComplexity(){ return "O(n + m)"; }
    @Override public String getDescription()    {
        return "Measures token-set overlap: |A∩B| / |A∪B|. Ideal for vocabulary comparison.";
    }
}
