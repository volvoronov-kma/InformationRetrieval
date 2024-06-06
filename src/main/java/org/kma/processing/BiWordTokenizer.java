package org.kma.processing;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CollectionUtils;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.StringUtils;
import org.kma.document.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class BiWordTokenizer implements Tokenizer {
    private final StanfordCoreNLP pipeline;
    private final static int NGRAM_SIZE = 2;


    public BiWordTokenizer() {
        Properties props = PropertiesUtils.asProperties(
                "annotators", "tokenize,ssplit,mwt,pos,lemma",
                "ssplit.isOneSentence", "true",
                "tokenize.whitespace", "true"
        );

        this.pipeline = new StanfordCoreNLP(props);
    }

    @Override
    public List<String> tokenize(Document document) {
        AtomicReference<String> lastWord = new AtomicReference<>();
        List<String> tokens = new ArrayList<>();
        document.getTextChunks().forEach(chunk -> {
            chunk = preprocessText(chunk);
            if (!chunk.isBlank()) {
                if (!StringUtils.isNullOrEmpty(lastWord.get())) {
                    chunk = lastWord.get() + " " + chunk;
                }
                CoreDocument coreDocument = new CoreDocument(chunk);
                pipeline.annotate(coreDocument);
                List<String> ngrams = getNgrams(coreDocument);
                lastWord.set(coreDocument.tokens().getLast().word());
                tokens.addAll(ngrams);
            }
        });
        return tokens;
    }

    @Override
    public String normalize(String text) {
        text = preprocessText(text);
        if (!text.isBlank()) {
            CoreDocument coreDocument = new CoreDocument(text);
            pipeline.annotate(coreDocument);
            List<String> ngrams = getNgrams(coreDocument);
            if (ngrams.isEmpty()) {
                return coreDocument.tokens().getFirst().word();
            }
            return ngrams.getFirst();
        }
        return text;
    }

    private List<String> getNgrams(CoreDocument coreDocument) {
        List<List<CoreLabel>>  labels = CollectionUtils.getNGrams(coreDocument.tokens(), NGRAM_SIZE, NGRAM_SIZE);
        List<String> tokens = new ArrayList<>();
        for (List<CoreLabel> label : labels) {
            tokens.add(label.getFirst().word() + " " + label.getLast().word());
        }
        return tokens;
    }

    private String preprocessText(String text) {
        text = text.replaceAll("[\\p{Punct}’]", "");
        return text.toLowerCase();
    }
}
