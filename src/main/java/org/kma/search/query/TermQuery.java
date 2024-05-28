package org.kma.search.query;

import org.kma.index.IncidenceMatrix;
import org.kma.index.SearchStructure;

import java.util.BitSet;
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

    @Override
    public BitSet evaluate(IncidenceMatrix incidenceMatrix) {
        return (BitSet) incidenceMatrix.findByTerm(term).clone();
    }
}
