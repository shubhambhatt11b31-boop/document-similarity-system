package org.docsim.model;

/**
 * Result of a single algorithm run.
 */
public class AlgorithmResult {

    private final String algorithmName;
    private final double score;           // 0.0 – 1.0
    private final long   executionTimeMs;
    private final String timeComplexity;
    private final String spaceComplexity;
    private final String description;

    public AlgorithmResult(String name, double score, long ms,
                           String timeC, String spaceC, String desc) {
        this.algorithmName  = name;
        this.score          = score;
        this.executionTimeMs = ms;
        this.timeComplexity = timeC;
        this.spaceComplexity = spaceC;
        this.description    = desc;
    }

    public String  getAlgorithmName()   { return algorithmName; }
    public double  getScore()           { return score; }
    public long    getExecutionTimeMs() { return executionTimeMs; }
    public String  getTimeComplexity()  { return timeComplexity; }
    public String  getSpaceComplexity() { return spaceComplexity; }
    public String  getDescription()     { return description; }

    /** Score as percentage string, e.g. "72.45%" */
    public String getScorePercent() {
        return String.format("%.2f%%", score * 100);
    }
}
