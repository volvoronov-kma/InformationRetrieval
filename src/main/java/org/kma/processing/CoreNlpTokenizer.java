package org.kma.processing;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.kma.document.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CoreNlpTokenizer implements Tokenizer {
    private final StanfordCoreNLP pipeline;

    public CoreNlpTokenizer() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        this.pipeline = new StanfordCoreNLP(props);
    }

    @Override
    public List<String> tokenize(Document document) {
        List<String> tokens = new ArrayList<>();
        document.getTextChunks().forEach(chunk -> {
            CoreDocument coreDocument = new CoreDocument(chunk);
            pipeline.annotate(coreDocument);
            coreDocument.tokens().forEach(token -> tokens.add(token.word()));
        });
        return tokens;
    }
}
