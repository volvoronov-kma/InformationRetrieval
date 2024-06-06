package org.kma.index;

import org.kma.document.Corpus;
import org.kma.document.Document;
import org.kma.index.domain.Posting;
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
    private final Map<String, HashMap<UUID, Posting>> postings;

    public InvertedIndex(Corpus corpus, Tokenizer tokenizer) {
        this.corpus = corpus;
        this.tokenizer = tokenizer;
        termFrequencies = new ConcurrentHashMap<>();
        postings = new ConcurrentHashMap<>();
    }

    @Override
    public Set<UUID> findByTerm(String term) {
        term = tokenizer.normalize(term);
        var documentPostings = postings.getOrDefault(term, new HashMap<>());
        var uuids = documentPostings.keySet();
        if (uuids.isEmpty()) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(uuids);
        }
    }

    @Override
    public Set<UUID> findByPhrase(List<String> phrase) {
        List<String> normalizedPhrase = new ArrayList<>();
        for (String term : phrase) {
            normalizedPhrase.add(tokenizer.normalize(term));
        }

        List<Map<UUID, Posting>> termPostings = new ArrayList<>();
        for (String term : normalizedPhrase) {
            if (!postings.containsKey(term)) {
                return Collections.emptySet();
            }
            termPostings.add(postings.get(term));
        }

        Set<UUID> commonDocIds = new HashSet<>(termPostings.get(0).keySet());
        for (Map<UUID, Posting> termPosting : termPostings) {
            commonDocIds.retainAll(termPosting.keySet());
        }

        Set<UUID> resultDocIds = new HashSet<>();
        for (UUID docId : commonDocIds) {
            if (phraseOccursInDocument(docId, termPostings, normalizedPhrase.size())) {
                resultDocIds.add(docId);
            }
        }

        return resultDocIds;
    }

    private boolean phraseOccursInDocument(UUID docId, List<Map<UUID, Posting>> termPostings, int phraseLength) {
        List<List<Integer>> positions = new ArrayList<>();
        for (Map<UUID, Posting> termPosting : termPostings) {
            positions.add(termPosting.get(docId).getPositions());
        }

        for (int pos : positions.get(0)) {
            boolean matches = true;
            for (int i = 1; i < phraseLength; i++) {
                if (!positions.get(i).contains(pos + i)) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                return true;
            }
        }
        return false;
    }


    public Corpus getCorpus() {
        return corpus;
    }

    @Override
    public Set<UUID> getAllDocIds() {
        return corpus.getAllDocumentIds();
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

    private void processDocument(Document doc) {
        List<String> tokens = tokenizer.tokenize(doc);
        IntStream.range(0, tokens.size())
                        .forEach(
                                position -> {
                                    String currentToken = tokens.get(position);
                                    termFrequencies.merge(currentToken, 1, Integer::sum);
                                    Map<UUID, Posting> tokenPostings = postings.computeIfAbsent(
                                            currentToken, k -> new HashMap<>());

                                    Posting posting = tokenPostings.computeIfAbsent(
                                            doc.getDocumentId(), k -> new Posting());
                                    posting.addOccurrenceAt(position);
                                }
                        );
    }
}
