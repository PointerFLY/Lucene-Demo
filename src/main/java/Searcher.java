import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class Searcher {

    private Analyzer analyzer = new StandardAnalyzer();
    private IndexSearcher indexSearcher;
    private Similarity similarity = new BM25Similarity();

    Analyzer getAnalyzer() {
        return analyzer;
    }

    IndexSearcher getIndexSearcher() {
        return indexSearcher;
    }

    void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    void setSimilarity(Similarity similarity) {
        this.similarity = similarity;
    }

    void readIndex() {
        try {
            DirectoryReader reader = DirectoryReader.open(FSDirectory.open(FileUtils.INDEX_DIR));
            indexSearcher = new IndexSearcher(reader);
            indexSearcher.setSimilarity(similarity);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, "Read index failed");
            System.exit(1);
        }
    }

    ScoreDoc[] search(Topic topic, int topHitsCount) {
        Query query = generateQuery(topic);

        try {
            return indexSearcher.search(query, topHitsCount).scoreDocs;
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, "Search error");
            System.exit(1);
        }

        return null;
    }

    private Query generateQuery(Topic topic) {
        // TODO: Optimization needed, query expansion.
        String fields[] = new String[] { Report.CONTENT };
        QueryParser parser = new MultiFieldQueryParser(fields, analyzer);

        try {
            String queryStr = topic.getTitle() + " " + topic.getDescription() + " " + topic.getRelevantNarrative();
            return parser.parse(QueryParser.escape(queryStr));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, "Parse query string failed.");
            System.exit(1);
        }

        return null;
    }
}
