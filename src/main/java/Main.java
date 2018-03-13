public class Main {

    public static void main(String[] args) {
        Indexer indexer = new Indexer();
        indexer.createIndex();

        Searcher searcher = new Searcher();
        searcher.readIndex();

        Evaluator evaluator = new Evaluator(searcher);
        evaluator.evaluateAll(true);
    }
}
