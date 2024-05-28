package org.kma.search.query;

import org.kma.index.SearchStructure;

import java.util.Set;
import java.util.UUID;

public interface BooleanQuery {
    Set<UUID> evaluate(SearchStructure searchStructure);
}
