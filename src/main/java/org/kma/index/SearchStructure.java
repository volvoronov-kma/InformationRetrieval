package org.kma.index;

import java.util.Set;
import java.util.UUID;

public interface SearchStructure {

    Set<UUID> findByTerm(String term);
    Set<String> getAllTerms();
}
