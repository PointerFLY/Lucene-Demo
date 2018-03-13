import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class Searcher {

    private Analyzer analyzer = new StandardAnalyzer();
    private IndexSearcher searcher;

    Analyzer getAnalyzer() {
        return analyzer;
    }

    void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    void setSimilarity(Similarity similarity) {
        searcher.setSimilarity(similarity);
    }

    void readIndex() {
        try {
            DirectoryReader reader = DirectoryReader.open(FSDirectory.open(FileUtils.INDEX_DIR));
            searcher = new IndexSearcher(reader);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, "Read index failed");
            System.exit(1);
        }
    }

    ArrayList<String> search(Topic topic, int topHitsCount) {
        Query query = generateQuery(topic);

        try {
            ScoreDoc[] hits = searcher.search(query, topHitsCount).scoreDocs;
            ArrayList<String> reportIds = new ArrayList<>();
            for (ScoreDoc hit: hits) {
                Document report = searcher.doc(hit.doc);
                String id = report.get(Report.ID);
                reportIds.add(id);
            }
            return reportIds;
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, "Search error");
            System.exit(1);
        }

        return null;
    }

    private Query generateQuery(Topic topic) {
        // TODO: Wise query generator
        QueryParser parser = new QueryParser("id", analyzer);

        try {
            return parser.parse(topic.getDescription());
        } catch (ParseException e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, "Parse query error");
            System.exit(1);
        }

        return null;
    }
}
