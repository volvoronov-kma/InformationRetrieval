package org.kma.index;

import org.kma.document.Corpus;
import org.kma.document.Document;
import org.kma.processing.Tokenizer;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Lexicon {

    private final Map<String, Integer> termFrequencies;
    private final Corpus corpus;
    private final Tokenizer tokenizer;
    // private final static Logger logger = LogManager.getLogger(Lexicon.class);


    public Lexicon(Corpus corpus, Tokenizer tokenizer) {
        this.termFrequencies = new ConcurrentHashMap<>();
        this.tokenizer = tokenizer;
        this.corpus = corpus;
    }

    public int getTermFrequencies(String token) {
        return termFrequencies.getOrDefault(token, 0);
    }

    public Map<String, Integer> getTermFrequencies() {
        return termFrequencies;
    }

    public Corpus getCorpus() {
        return corpus;
    }

    public void addDocuments(List<Document> docs) {
        docs.forEach(this::addDocument);
    }

    private void processDocument(Document doc) {
        List<String> tokens = tokenizer.tokenize(doc);
        addTerms(tokens);
    }

    public void readCorpus() {
        corpus.getAllDocuments()
                .forEach(this::processDocument);
    }

    public void addDocument(Document doc) {
        // logger.info("Adding document: " + doc.getDocumentId());
        corpus.addDocument(doc);
        processDocument(doc);
        // logger.info("Finished processing document: " + doc.getDocumentId());

    }

    public Map<String, Integer> getSortedTermFrequencies() {
        return getTermFrequencies().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new
                ));

    }

    public Statistics getStatistics() {
        Map<String, Integer> sortedTermFrequencies = getSortedTermFrequencies();
        Statistics statistics = new Statistics();
        statistics.setLargestFrequency(sortedTermFrequencies.entrySet()
                .stream()
                .findFirst()
                .get()
                .getValue());
        statistics.setTokensCount(sortedTermFrequencies.size());
        statistics.setTotalWordsCount(sortedTermFrequencies.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum());
        return statistics;
    }

    public void saveLexiconToCsv(String filePath) {
        Map<String, Integer> map = getSortedTermFrequencies();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the CSV headers
            writer.write("Key,Value");
            writer.newLine();

            // Write the key-value pairs
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing to the CSV file: " + e.getMessage());
        }
    }

    public void serializeLexicon(String filePath) {
        Map<String, Integer> map = getSortedTermFrequencies();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(map);
        } catch (IOException e) {
            System.err.println("Error serializing the map: " + e.getMessage());
        }
    }

    private void addTerms(List<String> tokens) {
        tokens.parallelStream().forEach(token -> termFrequencies.merge(token, 1, Integer::sum));
    }

    public static class Statistics {
        private long totalWordsCount;
        private long tokensCount;
        private long largestFrequency;


        @Override
        public String toString() {
            return "Statistics{" +
                    "totalWordsCount=" + totalWordsCount +
                    ", tokensCount=" + tokensCount +
                    ", largestFrequency=" + largestFrequency +
                    '}';
        }


        public long getTotalWordsCount() {
            return totalWordsCount;
        }

        public void setTotalWordsCount(long totalWordsCount) {
            this.totalWordsCount = totalWordsCount;
        }

        public long getTokensCount() {
            return tokensCount;
        }

        public void setTokensCount(long tokensCount) {
            this.tokensCount = tokensCount;
        }

        public long getLargestFrequency() {
            return largestFrequency;
        }

        public void setLargestFrequency(long largestFrequency) {
            this.largestFrequency = largestFrequency;
        }
    }
}
