import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;

import java.util.Arrays;
import java.util.List;

class CustomAnalyzer extends Analyzer {

    private static final List<String> STOP_WORDS = Arrays.asList(
            "what", "relevant", "done", "have", "how", "new", "must",
            "than", "some", "other", "stoic", "likely", "all"
    );

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new ClassicTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(source);
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        tokenStream = new EnglishMinimalStemFilter(tokenStream);
        tokenStream = new KStemFilter(tokenStream);
        CharArraySet stopSet = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
        stopSet.addAll(STOP_WORDS);
        tokenStream = new StopFilter(tokenStream, stopSet);
        tokenStream = new PorterStemFilter(tokenStream);
        return new TokenStreamComponents(source, tokenStream);
    }
}