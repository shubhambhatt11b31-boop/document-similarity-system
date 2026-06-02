package org.docsim.algorithms;

import org.docsim.model.AlgorithmResult;
import org.docsim.model.Document;


public interface SimilarityAlgorithm {


    double calculate(Document d1, Document d2);


    String getName();


    String getTimeComplexity();


    String getSpaceComplexity();


    String getDescription();


    default AlgorithmResult run(Document d1, Document d2) {
        long start = System.nanoTime();
        double score = calculate(d1, d2);
        long ms = (System.nanoTime() - start) / 1_000_000;
        return new AlgorithmResult(
            getName(), score, ms,
            getTimeComplexity(), getSpaceComplexity(), getDescription()
        );
    }
}
