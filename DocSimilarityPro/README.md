# 📄 Document Similarity Detection System — Pro v2.0

**Professional Desktop Application | DAA Final Year Project | Pure Java + Swing**

---

## 🔍 What Was Wrong with the Original Project

| Issue | Fix Applied |
|---|---|
| Spring Boot dependency | Removed entirely — pure Java |
| Only 3 algorithms | Expanded to **6 algorithms** |
| `MainFrame` printed to `System.out` only | Full **Swing GUI** with dark theme |
| No charts | **4 JFreeChart** charts (pie, bar, line, time) |
| No file picker | Drag-and-drop + file-chooser upload |
| `TextCleaner` regex bug (`[^a -zA-Z ]`) | Fixed to `[^a-zA-Z\\s]` |
| No stop-word removal | 67-word English stop-word filter |
| No preprocessing pipeline | Full pipeline: clean → tokenize → filter → TF |
| Rabin-Karp did brute-force search only | Rewritten as **rolling-hash trigram shingles** |
| LCS was O(m×n) uncapped | Capped at 500 tokens for large docs |

---

## 🏗️ Project Structure

```
DocSimilarityPro/
├── pom.xml                                ← Maven build (no Spring Boot)
├── README.md
├── documents/
│   ├── sample1.txt                        ← Test document A
│   └── sample2.txt                        ← Test document B
│
└── src/main/java/org/docsim/
    ├── Main.java                          ← Entry point (FlatLaf + Swing launch)
    ├── AppTheme.java                      ← Colours, fonts, design tokens
    │
    ├── model/
    │   ├── Document.java                  ← Holds text, tokens, TF map
    │   ├── AlgorithmResult.java           ← Single algorithm result
    │   └── SimilarityResult.java          ← Aggregated analysis result
    │
    ├── algorithms/
    │   ├── SimilarityAlgorithm.java       ← Interface with default run()
    │   ├── JaccardSimilarity.java         ← |A∩B|/|A∪B|        O(n+m)
    │   ├── RabinKarpSimilarity.java       ← Trigram rolling hash  O(n+m)
    │   ├── KMPSimilarity.java             ← Bigram KMP search     O(n+m)
    │   ├── CosineSimilarity.java          ← TF vector cosine      O(n+m+|V|)
    │   ├── LevenshteinSimilarity.java     ← Edit distance DP      O(n×m)
    │   └── TFIDFSimilarity.java           ← TF-IDF cosine         O(n+m+|V|)
    │
    ├── extractor/
    │   └── FileExtractor.java             ← TXT / PDF / DOCX extraction
    │
    ├── preprocessing/
    │   └── TextPreprocessor.java          ← Full pipeline + stop-words
    │
    ├── utils/
    │   ├── SimilarityEngine.java          ← Runs all 6 algorithms, weighted score
    │   └── ReportExporter.java            ← Export result as TXT report
    │
    └── ui/
        ├── MainWindow.java                ← Root JFrame, CardLayout, shared state
        ├── Sidebar.java                   ← Left navigation panel
        ├── components/
        │   └── UIComponents.java          ← RoundedPanel, GradientButton, ScoreBar…
        └── panels/
            ├── HomePanel.java             ← Dashboard landing page
            ├── UploadPanel.java           ← Drag-and-drop file upload
            ├── ResultsPanel.java          ← Full results dashboard
            ├── AnalyticsPanel.java        ← 4× JFreeChart charts
            ├── AlgorithmComparePanel.java ← Side-by-side comparison table
            └── AboutPanel.java            ← Project info & instructions
```

---

## ⚙️ Requirements

| Tool | Version |
|---|---|
| Java JDK | 17 or later |
| Apache Maven | 3.8+ |
| RAM | 256 MB minimum |
| OS | Windows / Linux / macOS |

---

## 🚀 Build & Run

### Option 1 — Maven (Recommended)

```bash
# 1. Enter project directory
cd DocSimilarityPro

# 2. Build fat JAR (includes ALL dependencies)
mvn clean package

# 3. Run
java -jar target/DocSimilarityPro-2.0.0-runnable.jar
```

### Option 2 — IntelliJ IDEA

1. Open the `DocSimilarityPro` folder as a Maven project
2. Let IntelliJ download dependencies
3. Run `src/main/java/org/docsim/Main.java`

### Option 3 — VS Code

1. Install "Extension Pack for Java"
2. Open `DocSimilarityPro` folder
3. Click **Run** on `Main.java`

---

## 📖 How to Use

1. Launch the app → **Home Dashboard** opens
2. Click **Upload Documents** in the sidebar
3. Drop or click to select **Document A** (TXT / PDF / DOCX)
4. Drop or click to select **Document B**
5. Click **Run Similarity Analysis** (green gradient button)
6. View results on the **Similarity Results** panel
7. Click **Analytics & Charts** for pie, bar, and line charts
8. Click **Algorithm Compare** for the detailed comparison table
9. Click **Export TXT Report** to save a text report

---

## 🧮 Algorithms & Final Score

```
Final = 0.20 × Jaccard
      + 0.15 × Rabin-Karp
      + 0.15 × KMP
      + 0.20 × Cosine
      + 0.15 × Levenshtein
      + 0.15 × TF-IDF
```

| # | Algorithm | Approach | Time | Space |
|---|---|---|---|---|
| 1 | Jaccard | Token set intersection/union | O(n+m) | O(n+m) |
| 2 | Rabin-Karp | Trigram rolling-hash shingles | O(n+m) | O(n+m) |
| 3 | KMP | Bigram prefix-table pattern search | O(n+m) | O(m) |
| 4 | Cosine | TF vector dot product / magnitudes | O(n+m+\|V\|) | O(\|V\|) |
| 5 | Levenshtein | Edit-distance DP on token sequences | O(n×m) | O(min(n,m)) |
| 6 | TF-IDF | IDF-weighted cosine similarity | O(n+m+\|V\|) | O(\|V\|) |

---

## 🎨 UI Features

- **Dark theme** via FlatLaf `FlatDarkLaf`
- **Sidebar** navigation (6 pages)
- **Rounded cards** with surface/surface2 depth
- **Gradient buttons** (cyan→blue, green)
- **Score ring** (custom-painted donut arc)
- **Score bars** (animated fill)
- **4 JFreeChart** visualisations (pie, bar ×2, line)
- **Keyword cloud** of shared terms
- **Export** to formatted TXT report
- **Drag-and-drop** file zones with dashed animated borders
- **SwingWorker** background processing (UI never freezes)

---

## 📦 Dependencies (auto-downloaded by Maven)

```xml
FlatLaf     3.4       — Modern Swing dark/light theme
JFreeChart  1.5.4     — Charts (pie, bar, line)
PDFBox      3.0.2     — PDF text extraction
Apache POI  5.2.5     — DOCX text extraction
SLF4J       2.0.13    — Logging (suppresses PDFBox verbose output)
```

---

## 🧪 Test with Sample Documents

The `documents/` folder contains two similar AI/ML documents.
Upload `sample1.txt` as Document A and `sample2.txt` as Document B
to see a realistic similarity analysis with shared vocabulary.

---

*DocSimilarity Pro v2.0 — Built with pure Java, no Spring Boot, no web frameworks.*
