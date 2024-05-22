package org.kma;

import org.kma.document.Corpus;
import org.kma.document.Document;
import org.kma.document.reading.TxtDocumentReader;
import org.kma.index.Lexicon;
import org.kma.processing.CoreNlpTokenizer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        TxtDocumentReader reader = new TxtDocumentReader();
        Lexicon lexicon = new Lexicon(new Corpus(), new CoreNlpTokenizer());

        File dir = new File("src/main/resources/documents");
        try {
            List<Document> docs = reader.readAll(Arrays.stream(Objects.requireNonNull(dir.listFiles())).toList());
            lexicon.addDocuments(docs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Integer> termFrequencies = lexicon.getTermFrequencies();


        Map<String, Integer> sortedTermFrequencies = lexicon.getSortedTermFrequencies();
        Lexicon.Statistics statistics = lexicon.getStatistics();

        System.out.println(statistics);
        lexicon.saveLexiconToCsv("results/lexicon.csv");
        lexicon.serializeLexicon("results/lexicon.ser");
    }
}