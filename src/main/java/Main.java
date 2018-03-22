import java.io.IOException;
import java.util.Scanner;

//import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilterFactory;

public class Main {

    public static void main(String[] args) {
        FileUtils.initialize();
        try {
			Analyzer stemmingAnalyzer = CustomAnalyzer.builder().withTokenizer(StandardTokenizerFactory.class).addTokenFilter(LowerCaseFilterFactory.class).addTokenFilter(PorterStemFilterFactory.class).addTokenFilter(StandardFilterFactory.class).build();
	        Indexer indexer = new Indexer();
	        //indexer.setAnalyzer(new StandardAnalyzer());
	        indexer.setAnalyzer(stemmingAnalyzer);

			System.out.println("\nPress 1 if you wan to re index the files (note: this will take 2 to 10 minutes depending on the processor\n");

			Integer option = 0;
			@SuppressWarnings("resource")
			Scanner scan = new Scanner(System.in);
			String option1 = scan.nextLine();
			try {

				option = Integer.valueOf(option1);
			}
			catch (NumberFormatException e) {
				option = 0;
				
			}
			if (option == 1)
			{
				System.out.println("\nIndexing Started\n");
		        indexer.createIndex(); //creates the index but once created we can comment it out as doesn't need to create index again
				System.out.println("\nIndexing Completed\n");

			}
			

	        Searcher searcher = new Searcher();
	        //searcher.setAnalyzer(new StandardAnalyzer());
	        searcher.setAnalyzer(stemmingAnalyzer);
	        // TODO: Custom similarity.
	        searcher.setSimilarity(new BM25Similarity());
	        searcher.readIndex();

	        Evaluator evaluator = new Evaluator(searcher);
	        evaluator.generateResults();
			System.out.println("Everything Completed Now see Results in temp\\Results \n");


		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    }
}
