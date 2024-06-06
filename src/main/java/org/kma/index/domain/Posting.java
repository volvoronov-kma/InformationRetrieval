package org.kma.index.domain;

import java.util.ArrayList;
import java.util.List;

public class Posting {

    private Integer frequency;
    private final List<Integer> positions;

    public Posting() {
        this.frequency = 0;
        this.positions = new ArrayList<>();
    }

    public Posting addOccurrenceAt(int position) {
        frequency++;
        positions.add(position);
        return this;
    }


    public Integer getFrequency() {
        return frequency;
    }

    public List<Integer> getPositions() {
        return positions;
    }
}
