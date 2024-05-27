package org.kma.processing;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.PropertiesUtils;
import org.kma.document.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CoreNlpTokenizer implements Tokenizer {
    private final StanfordCoreNLP pipeline;

    public CoreNlpTokenizer() {
        Properties props = PropertiesUtils.asProperties(
                "annotators", "tokenize,ssplit,mwt,pos,lemma",
                "ssplit.isOneSentence", "true",
                "tokenize.whitespace", "true"
        );

        this.pipeline = new StanfordCoreNLP(props);
    }

    @Override
    public List<String> tokenize(Document document) {
        List<String> tokens = new ArrayList<>();
        document.getTextChunks().forEach(chunk -> {
            chunk = preprocessText(chunk);
            if (!chunk.isBlank()) {
                CoreDocument coreDocument = new CoreDocument(chunk);
                pipeline.annotate(coreDocument);
                coreDocument.tokens().forEach(token -> tokens.add(token.word()));
            }
        });
        return tokens;
    }

    private String preprocessText(String text) {
        text = text.replaceAll("\\p{Punct}", "");
        return text.toLowerCase();
    }
}
