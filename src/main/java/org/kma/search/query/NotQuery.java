package org.kma.search.query;

import org.kma.index.IncidenceMatrix;
import org.kma.index.SearchStructure;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NotQuery implements BooleanQuery {

    private BooleanQuery query;

    public NotQuery(BooleanQuery query) {
        this.query = query;
    }

    @Override
    public Set<UUID> evaluate(SearchStructure searchStructure) {
        Set<UUID> allDocs = searchStructure.getAllDocIds();
        Set<UUID> queryResult = query.evaluate(searchStructure);
        allDocs.removeAll(queryResult);
        return allDocs;
    }

    @Override
    public BitSet evaluate(IncidenceMatrix incidenceMatrix) {
        BitSet originalBitSet = query.evaluate(incidenceMatrix);
        BitSet clone = (BitSet) originalBitSet.clone();
        clone.flip(0, incidenceMatrix.corpusSize());
        return clone;
    }

}
