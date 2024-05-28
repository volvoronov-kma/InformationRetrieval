package org.kma.index;

import org.kma.document.Corpus;
import org.kma.document.Document;
import org.kma.processing.Tokenizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class IncidenceMatrix {
    private final Corpus corpus;
    private final Tokenizer tokenizer;
    private final Map<UUID, Integer> uuidToIndex;
    private final Map<Integer, UUID> indexToUuid;
    private final Map<String, BitSet> matrix;


    public IncidenceMatrix(Corpus corpus, Tokenizer tokenizer) {
        this.corpus = corpus;
        this.tokenizer = tokenizer;
        matrix = new ConcurrentHashMap<>();
        uuidToIndex = new ConcurrentHashMap<>();
        indexToUuid = new ConcurrentHashMap<>();
    }

    public Map<String, BitSet> getOrComputeIncidenceMatrix() {
        if (matrix.isEmpty()) {
            enumerateDocuments();
            buildIncidenceMatrix();
        }
        return matrix;
    }

    public BitSet findByTerm(String term) {
        term = tokenizer.normalize(term);
        BitSet bitSet = matrix.getOrDefault(term, new BitSet(corpusSize()));
        return (BitSet) bitSet.clone();
    }

    public int corpusSize() {
        return corpus.size();
    }

    public Set<UUID> getDocumentsByBitSet(BitSet bitSet) {
        Set<UUID> documents = new HashSet<>();
        int index = bitSet.nextSetBit(0);
        while (index != -1) {
            documents.add(indexToUuid.get(index));
            index = bitSet.nextSetBit(index + 1);
        }
        return documents;
    }

    public void saveToCsv(String filePath) {
        Map<String, BitSet> map = this.matrix;
        Map<Integer, UUID> indexToUuid = this.indexToUuid;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the CSV headers
            writer.write("Term,");
            for (int i = 0; i < corpusSize(); i++) {
                writer.write(indexToUuid.get(i) + ",");
            }
            writer.newLine();

            // Write the key-value pairs
            for (Map.Entry<String, BitSet> entry : map.entrySet()) {
                writer.write(entry.getKey() + "," + bitSetToString(entry.getValue()));
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing to the CSV file: " + e.getMessage());
        }
    }

    private String bitSetToString(BitSet bitSet) {
        final StringBuilder buffer = new StringBuilder(corpusSize());
        IntStream.range(0, corpusSize())
                .mapToObj(i -> bitSet.get(i) ? '1' : '0')
                .forEach(character -> {
                    buffer.append(character);
                    buffer.append(',');
                });
        return buffer.toString();
    }

    private void enumerateDocuments() {
        int i = 0;
        for (Document document : corpus.getAllDocuments()) {
            uuidToIndex.putIfAbsent(document.getDocumentId(), i);
            indexToUuid.putIfAbsent(i, document.getDocumentId());
            i++;
        }
    }

    private void buildIncidenceMatrix() {
        int vectorSize = uuidToIndex.size();
        corpus.getAllDocuments().parallelStream().forEach(d -> {
            var tokens = tokenizer.tokenize(d);
            tokens.forEach(t -> matrix.computeIfAbsent(t, k ->
                    new BitSet(vectorSize)).set(uuidToIndex.get(d.getDocumentId())));
        });
    }
}
