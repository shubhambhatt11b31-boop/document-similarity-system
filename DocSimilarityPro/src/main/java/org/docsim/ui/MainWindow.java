package org.docsim.ui;

import org.docsim.AppTheme;
import org.docsim.model.Document;
import org.docsim.model.SimilarityResult;
import org.docsim.ui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class MainWindow extends JFrame {


    private final List<Document>   documents      = new ArrayList<>();
    private       SimilarityResult latestResult   = null;


    private final JPanel  contentArea = new JPanel(new CardLayout());
    private final Sidebar sidebar;


    private final HomePanel          homePanel;
    private final UploadPanel        uploadPanel;
    private final ResultsPanel       resultsPanel;
    private final AnalyticsPanel     analyticsPanel;
    private final AlgorithmComparePanel comparePanel;
    //private final AboutPanel         aboutPanel;

    public MainWindow() {
        super("Document Similarity Detection System — Professional Desktop Edition");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BG);


        homePanel    = new HomePanel(this);
        uploadPanel  = new UploadPanel(this);
        resultsPanel = new ResultsPanel(this);
        analyticsPanel  = new AnalyticsPanel(this);
        comparePanel = new AlgorithmComparePanel(this);
        //aboutPanel   = new AboutPanel();

        contentArea.setBackground(AppTheme.BG);
        contentArea.add(homePanel,     "home");
        contentArea.add(uploadPanel,   "upload");
        contentArea.add(resultsPanel,  "results");
        contentArea.add(analyticsPanel,"analytics");
        contentArea.add(comparePanel,  "compare");



        sidebar = new Sidebar(this::showPage);


        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG);
        root.add(sidebar,     BorderLayout.WEST);
        root.add(contentArea, BorderLayout.CENTER);

        setContentPane(root);
    }

    /** Switch to a named page. */
    public void showPage(String id) {
        ((CardLayout) contentArea.getLayout()).show(contentArea, id);
        sidebar.navigate(id);
    }



    public List<Document> getDocuments()    { return documents; }

    public void setDocuments(List<Document> docs) {
        documents.clear();
        documents.addAll(docs);
    }

    public SimilarityResult getLatestResult() { return latestResult; }


    public void onAnalysisComplete(SimilarityResult result) {
        this.latestResult = result;
        resultsPanel.refresh(result);
        analyticsPanel.refresh(result);
        comparePanel.refresh(result);
        showPage("results");

    }
    public void navigate(String page) {
        ((CardLayout) contentArea.getLayout()).show(contentArea, page);
    }

}
