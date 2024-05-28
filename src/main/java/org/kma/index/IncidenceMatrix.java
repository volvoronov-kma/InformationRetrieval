package org.kma.index;

import org.kma.document.Corpus;
import org.kma.document.Document;
import org.kma.processing.Tokenizer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IncidenceMatrix {
    private final Corpus corpus;
    private final Tokenizer tokenizer;
    private final Map<UUID, Integer> docIndices;
    private final Map<String, BitSet> matrix;


    public IncidenceMatrix(Corpus corpus, Tokenizer tokenizer) {
        this.corpus = corpus;
        this.tokenizer = tokenizer;
        matrix = new ConcurrentHashMap<>();
        docIndices = new ConcurrentHashMap<>();
    }

    public Map<String, BitSet> getIncidenceMatrix() {
        if (matrix.isEmpty()) {
            enumerateDocuments();
            buildIncidenceMatrix();
        }
        return matrix;
    }

    private void enumerateDocuments() {
        int i = 0;
        for (Document document : corpus.getAllDocuments()) {
            docIndices.putIfAbsent(document.getDocumentId(), i);
            i++;
        }
    }

    private void buildIncidenceMatrix() {
        int vectorSize = docIndices.size();
        corpus.getAllDocuments().parallelStream().forEach(d -> {
            var tokens = tokenizer.tokenize(d);
            tokens.forEach(t -> matrix.computeIfAbsent(t, k ->
                    new BitSet(vectorSize)).set(docIndices.get(d.getDocumentId())));
        });
    }
}
