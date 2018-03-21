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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

class Indexer {

    private Analyzer analyzer = new StandardAnalyzer();
    private ThreadPoolExecutor executor;

    Indexer() {
        executor = new ThreadPoolExecutor(
                8,
                64,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(3000)
        );
    }

    Analyzer getAnalyzer() {
        return analyzer;
    }

    void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    void createIndex() {
        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            Directory dir = FSDirectory.open(FileUtils.INDEX_DIR);
            IndexWriter writer = new IndexWriter(dir, config);

            ArrayList<Path> paths = FileUtils.getAllReportFiles();
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
