import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
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
        String fields[] = new String[] { Report.CONTENT };
        QueryParser parser = new MultiFieldQueryParser(fields, analyzer);

        try {
            Query titleQuery = parser.parse(QueryParser.escape(topic.getTitle()));
            titleQuery = new BoostQuery(titleQuery, 4.2f);
            Query descriptionQuery = parser.parse(QueryParser.escape(topic.getDescription()));
            descriptionQuery = new BoostQuery(descriptionQuery, 1.8f);
            Query narrativeQuery = parser.parse(QueryParser.escape("a" + topic.getRelevantNarrative()));

            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(titleQuery, BooleanClause.Occur.MUST);
            builder.add(descriptionQuery, BooleanClause.Occur.MUST);
            builder.add(narrativeQuery, BooleanClause.Occur.MUST);

            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, "Parse query string failed.");
            System.exit(1);
        }

        return null;
    }
}
