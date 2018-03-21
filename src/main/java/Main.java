import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;

public class Main {

    public static void main(String[] args) {
        FileUtils.initialize();

        Indexer indexer = new Indexer();
        indexer.setAnalyzer(new StandardAnalyzer());
        indexer.createIndex();

        Searcher searcher = new Searcher();
        searcher.setAnalyzer(new StandardAnalyzer());
        searcher.setSimilarity(new BM25Similarity());
        searcher.readIndex();

        Evaluator evaluator = new Evaluator(searcher);
        evaluator.generateResults();
    }
}
