import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class Indexer {

    private Analyzer analyzer = new StandardAnalyzer();

    Analyzer getAnalyzer() {
        return analyzer;
    }

    void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    void createIndex() {
        ArrayList<Path> paths = FileUtils.getAllReportFiles();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            Directory dir = FSDirectory.open(FileUtils.INDEX_DIR);
            IndexWriter writer = new IndexWriter(dir, config);

            for (Path path: paths) {
                executor.execute(() -> {
                    ArrayList<Report> reports = FileParser.readReport(path);

                    // TODO: Topics, more members, add document wisely.
                    for (Report report: reports) {
                        StringField id = new StringField(Report.ID, report.getId(), Field.Store.YES);
                        TextField content = new TextField(Report.CONTENT, report.getContent(), Field.Store.NO);

                        Document doc = new Document();
                        doc.add(id);
                        doc.add(content);
                        
                        try {
                            writer.addDocument(doc);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Logger.getGlobal().log(Level.SEVERE, "Index failed.");
                            System.exit(1);
                        }
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, "Index failed: " + e.toString());
            System.exit(1);
        }
    }
}
