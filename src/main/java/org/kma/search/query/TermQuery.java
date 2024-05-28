package org.kma.search.query;

import org.kma.index.SearchStructure;

import java.util.Set;
import java.util.UUID;

public class TermQuery implements BooleanQuery {
    private String term;

    public TermQuery(String term) {
        this.term = term;
    }

    @Override
    public Set<UUID> evaluate(SearchStructure searchStructure) {
        return searchStructure.findByTerm(term);
    }
}
