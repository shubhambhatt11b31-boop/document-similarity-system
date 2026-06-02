package org.docsim.preprocessing;

import org.docsim.model.Document;

import java.util.*;

/**
 * Full text preprocessing pipeline:
 *   1. Lowercase
 *   2. Remove punctuation / special characters
 *   3. Tokenize on whitespace
 *   4. Remove stop-words
 *   5. (optional) Very light stemming — strip common suffixes
 *   6. Build term-frequency map
 */
public class TextPreprocessor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a","an","the","and","or","but","if","in","on","at","to","for","of","with",
        "by","from","up","about","into","through","during","is","are","was","were",
        "be","been","being","have","has","had","do","does","did","will","would",
        "shall","should","may","might","must","can","could","not","no","nor","so",
        "yet","both","either","neither","each","few","more","most","other","some",
        "such","than","too","very","just","also","as","its","it","this","that",
        "these","those","their","there","them","they","he","she","we","i","you","my",
        "your","our","his","her","what","which","who","whom","when","where","why","how"
    ));

    /**
     * Process a document in-place:
     * sets cleanText, tokens, and termFrequency.
     */
    public static void process(Document doc) {
        String text = doc.getRawText();
        if (text == null || text.isBlank()) {
            doc.setCleanText("");
            doc.setTokens(Collections.emptyList());
            doc.setTermFrequency(Collections.emptyMap());
            return;
        }

        // 1. Lowercase
        text = text.toLowerCase();

        // 2. Keep only letters and spaces (removes numbers, punctuation)
        text = text.replaceAll("[^a-z\\s]", " ");

        // 3. Collapse whitespace
        text = text.replaceAll("\\s+", " ").trim();
        doc.setCleanText(text);

        // 4. Tokenize
        String[] rawTokens = text.split(" ");

        // 5. Filter stop-words + minimum length 2
        List<String> tokens = new ArrayList<>();
        for (String t : rawTokens) {
            if (t.length() >= 2 && !STOP_WORDS.contains(t)) {
                tokens.add(t);
            }
        }

        doc.setTokens(tokens);

        // 6. Build TF map
        Map<String, Integer> tf = new LinkedHashMap<>();
        for (String t : tokens) tf.merge(t, 1, Integer::sum);
        // Sort by frequency descending
        tf = sortByValue(tf);
        doc.setTermFrequency(tf);
    }

    /** Returns the stop-word set (for display / UI). */
    public static Set<String> getStopWords() {
        return Collections.unmodifiableSet(STOP_WORDS);
    }

    private static Map<String, Integer> sortByValue(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        Map<String, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : list) sorted.put(e.getKey(), e.getValue());
        return sorted;
    }
}
