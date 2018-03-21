import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class Evaluator {

    private Searcher searcher;

    private static final int NUM_TOP_HITS = 1000;
    private static ArrayList<Topic> topics = FileParser.readTopic(FileUtils.TOPICS_FILE);

    Evaluator(Searcher searcher) {
        this.searcher = searcher;
    }

    void generateResults() {
        try {
            Files.createFile(FileUtils.RESULTS_FILE);
            BufferedWriter writer = new BufferedWriter(new FileWriter(FileUtils.RESULTS_FILE.toFile()));

            for (Topic topic: topics) {
                ScoreDoc[] hits = searcher.search(topic, NUM_TOP_HITS);
                for (int i = 0; i < hits.length; i++) {
                    ScoreDoc hit = hits[i];
                    Document doc = searcher.getIndexSearcher().doc(hit.doc);
                    String reportId = doc.get(Report.ID);
                    int rank = i + 1;

                    // TREC_eval results file format: query-id Q0 document-id rank score STANDARD
                    String line = String.format("%d Q0 %s %d %f STANDARD\n", topic.getId(), reportId, rank, hit.score);
                    writer.write(line);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, "Create results file failed.");
            System.exit(1);
        }
    }
}
