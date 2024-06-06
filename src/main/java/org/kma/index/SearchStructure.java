package org.kma.index;

import org.kma.document.Corpus;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SearchStructure {

    Set<UUID> findByTerm(String term);
    Set<UUID> findByPhrase(List<String> phrase);
    Set<UUID> getAllDocIds();
    Set<String> getAllTerms();
}
