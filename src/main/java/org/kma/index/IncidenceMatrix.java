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
    private final Map<String, List<Boolean>> matrix;
    private final Set<String> vocabulary;
    private final Map<UUID, Set<String>> docVocabularies;


    public IncidenceMatrix(Corpus corpus, Tokenizer tokenizer) {
        this.corpus = corpus;
        this.tokenizer = tokenizer;
        matrix = new ConcurrentHashMap<>();
        docIndices = new ConcurrentHashMap<>();
        vocabulary = new HashSet<>();
        docVocabularies = new ConcurrentHashMap<>();
    }

    public Map<String, List<Boolean>> getIncidenceMatrix() {
        if (matrix.isEmpty()) {
            enumerateDocuments();
            buildVocabulary();
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
        vocabulary.forEach(token -> {
                    ArrayList<Boolean> list = new ArrayList<>(vectorSize);
                    corpus.getAllDocuments().forEach(doc -> {
                        int index = docIndices.get(doc.getDocumentId());
                        if (docVocabularies.get(doc.getDocumentId()).contains(token)) {
                            list.add(index, true);
                        } else {
                            list.add(index, false);
                        }
                    });
                    matrix.put(token, list);
                }
        );
    }

    private void buildVocabulary() {
        corpus.getAllDocuments().forEach(doc -> {
            Set<String> tokens = new HashSet<>(tokenizer.tokenize(doc));
            docVocabularies.putIfAbsent(doc.getDocumentId(), tokens);
            vocabulary.addAll(tokens);
        });
    }
}
