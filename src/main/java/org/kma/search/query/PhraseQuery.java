package org.kma.search.query;

import org.kma.index.IncidenceMatrix;
import org.kma.index.SearchStructure;

import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PhraseQuery implements BooleanQuery {
    private List<String> terms;

    public PhraseQuery(List<String> terms) {
        this.terms = terms;
    }


    @Override
    public Set<UUID> evaluate(SearchStructure searchStructure) {
        return searchStructure.findByPhrase(terms);
    }

    @Override
    public BitSet evaluate(IncidenceMatrix incidenceMatrix) {
        throw new UnsupportedOperationException();
    }
}
