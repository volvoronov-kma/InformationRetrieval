package org.kma.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Document {
    private final List<String> textChunks;
    private final UUID docId;
    private final String title;

    public Document(String text, String title) {
        docId = UUID.randomUUID();
        title = title.trim();
        this.title = title;
        textChunks = new ArrayList<>();
        textChunks.add(text);
    }

    public Document(List<String> chunks, String title) {
        docId = UUID.randomUUID();
        title = title.trim();
        this.title = title;
        textChunks = chunks;
    }

    public List<String> getTextChunks() {
        return textChunks;
    }

    public UUID getDocumentId() {
        return docId;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(textChunks, document.textChunks) && Objects.equals(docId, document.docId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textChunks, docId);
    }
}
