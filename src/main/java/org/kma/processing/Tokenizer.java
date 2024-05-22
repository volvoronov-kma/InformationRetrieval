package org.kma.processing;

import org.kma.document.Document;

import java.util.List;

public interface Tokenizer {

    public List<String> tokenize(Document document);
}
