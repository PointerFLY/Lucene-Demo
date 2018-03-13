import java.util.ArrayList;

class Evaluator {

    private Searcher searcher;

    private static final int NUM_TOP_HITS = 1000;
    private static ArrayList<Topic> topics = FileParser.readTopic(FileUtils.TOPICS_FILE);

    Evaluator(Searcher searcher) {
        this.searcher = searcher;
    }

    void evaluateAll(boolean printEachQuery) {
        for (Topic topic: topics) {
            ArrayList<String> ids = searcher.search(topic, NUM_TOP_HITS);
            // TODO: Generate TREC files and then evaluate with trec_val
        }
    }

}
