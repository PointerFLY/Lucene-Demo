import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.Arrays;
import java.util.List;

class CustomAnalyzer extends Analyzer {

    private static final List<String> STOP_WORDS = Arrays.asList(
            "how", "when", "you", "from", "you", "can", "get", "relevant"
    );

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        // TODO: Fine tune analyzer
        Tokenizer source = new ClassicTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(source);
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        tokenStream = new EnglishMinimalStemFilter(tokenStream);
        tokenStream = new KStemFilter(tokenStream);
        tokenStream = new PorterStemFilter(tokenStream);
        CharArraySet stopSet = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
        stopSet.addAll(STOP_WORDS);
        tokenStream = new StopFilter(tokenStream, stopSet);
        return new TokenStreamComponents(source, tokenStream);
    }
}