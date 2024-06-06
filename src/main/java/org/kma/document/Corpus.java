package org.kma.document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Corpus {

    private final Map<UUID, Document> documents;

    public Corpus() {
        this.documents = new ConcurrentHashMap<>();
    }

    public void addDocument(Document document) {
        this.documents.put(document.getDocumentId(), document);
    }

    public void addDocuments(Collection<Document> documents) {
        documents.forEach(this::addDocument);
    }

    public Document getDocument(UUID docId) {
        return this.documents.get(docId);
    }

    public Collection<Document> getAllDocuments() {
        return new HashSet<>(this.documents.values());
    }

    public Set<UUID> getAllDocumentIds() {
        return new HashSet<>(this.documents.keySet());
    }

    public int size() {
        return this.documents.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Corpus corpus = (Corpus) o;
        return Objects.equals(documents, corpus.documents);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(documents);
    }
}
