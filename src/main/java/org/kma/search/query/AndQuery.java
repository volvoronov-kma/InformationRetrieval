package org.kma.search.query;

import org.kma.index.IncidenceMatrix;
import org.kma.index.SearchStructure;

import java.util.BitSet;
import java.util.Set;
import java.util.UUID;

public class AndQuery implements BooleanQuery {

    private BooleanQuery left;
    private BooleanQuery right;

    public AndQuery(BooleanQuery left, BooleanQuery right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Set<UUID> evaluate(SearchStructure searchStructure) {
        Set<UUID> leftResult = left.evaluate(searchStructure);
        Set<UUID> rightResult = right.evaluate(searchStructure);
        leftResult.retainAll(rightResult);
        return leftResult;
    }

    @Override
    public BitSet evaluate(IncidenceMatrix incidenceMatrix) {
        BitSet leftResult = left.evaluate(incidenceMatrix);
        BitSet rightResult = right.evaluate(incidenceMatrix);

        BitSet bitSet = (BitSet) leftResult.clone();
        bitSet.and(rightResult);

        return bitSet;
    }
}
