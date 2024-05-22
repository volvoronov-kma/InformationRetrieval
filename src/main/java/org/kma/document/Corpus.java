package org.kma.document;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
        return this.documents.values();
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
