package org.kma.index;

import org.kma.document.Corpus;
import org.kma.document.Document;
import org.kma.processing.Tokenizer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InvertedIndex {

    private final Corpus corpus;
    private final Tokenizer tokenizer;
    private final Map<String, Integer> termFrequencies;
    private final Map<String, HashSet<UUID>> mappings;

    public InvertedIndex(Corpus corpus, Tokenizer tokenizer) {
        this.corpus = corpus;
        this.tokenizer = tokenizer;
        termFrequencies = new ConcurrentHashMap<>();
        mappings = new ConcurrentHashMap<>();
    }

    public void readCorpus() {
        corpus.getAllDocuments().forEach(this::processDocument);
    }

    public void addDocuments(List<Document> docs) {
        docs.forEach(this::addDocument);
    }

    public void addDocument(Document doc) {
        corpus.addDocument(doc);
        processDocument(doc);
    }

    private void processDocument(Document doc) {
        List<String> tokens = tokenizer.tokenize(doc);
        addTerms(tokens);
        addMappings(tokens, doc);
    }

    private void addMappings(List<String> tokens, Document document) {
        tokens.parallelStream().forEach(
                token -> mappings.computeIfAbsent(
                        token, k -> new HashSet<>())
                        .add(document.getDocumentId()));
    }

    private void addTerms(List<String> tokens) {
        tokens.parallelStream().forEach(token -> termFrequencies.merge(token, 1, Integer::sum));
    }
}
