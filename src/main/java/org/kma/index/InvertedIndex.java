package org.kma.index;

import org.kma.document.Corpus;
import org.kma.document.Document;
import org.kma.processing.Tokenizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class InvertedIndex implements SearchStructure {

    private final Corpus corpus;
    private final Tokenizer tokenizer;
    private final Map<String, Integer> termFrequencies;
    private final Map<String, HashSet<UUID>> postings;

    public InvertedIndex(Corpus corpus, Tokenizer tokenizer) {
        this.corpus = corpus;
        this.tokenizer = tokenizer;
        termFrequencies = new ConcurrentHashMap<>();
        postings = new ConcurrentHashMap<>();
    }

    @Override
    public Set<UUID> findByTerm(String term) {
        term = tokenizer.normalize(term);
        var uuids = postings.getOrDefault(term, new HashSet<>());
        return new HashSet<>(uuids);
    }
    
    @Override
    public Set<String> getAllTerms() {
        return postings.keySet();
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

    public void saveToCsv(String filePath) {
        Map<String, HashSet<UUID>> map = this.postings;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the CSV headers
            writer.write("Term,Postings");
            writer.newLine();

            // Write the key-value pairs
            for (Map.Entry<String, HashSet<UUID>> entry : map.entrySet()) {
                writer.write(entry.getKey() + ", \"" + entry.getValue().toString() + "\"");
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing to the CSV file: " + e.getMessage());
        }
    }

    private void processDocument(Document doc) {
        List<String> tokens = tokenizer.tokenize(doc);
        addTerms(tokens);
        addPostings(tokens, doc);
    }

    private void addPostings(List<String> tokens, Document document) {
        tokens.parallelStream().forEach(
                token -> postings.computeIfAbsent(
                        token, k -> new HashSet<>())
                        .add(document.getDocumentId()));
    }

    private void addTerms(List<String> tokens) {
        tokens.parallelStream().forEach(token -> termFrequencies.merge(token, 1, Integer::sum));
    }
}
