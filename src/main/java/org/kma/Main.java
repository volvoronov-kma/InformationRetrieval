package org.kma;

import org.kma.document.Corpus;
import org.kma.document.Document;
import org.kma.document.reading.TxtDocumentReader;
import org.kma.index.IncidenceMatrix;
import org.kma.index.InvertedIndex;
import org.kma.processing.CoreNlpTokenizer;
import org.kma.search.parser.BooleanExpressionParser;
import org.kma.search.query.BooleanQuery;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String QUERY_SIMPLE = "Cleopatra AND Caesar";
        String QUERY_COMPLEX = "(Denmark OR CLEOPATRA) OR Venice";

        TxtDocumentReader reader = new TxtDocumentReader();
        InvertedIndex index = new InvertedIndex(new Corpus(), new CoreNlpTokenizer());

        File dir = new File("src/main/resources/documents");
        try {
            long start = System.currentTimeMillis();

            List<Document> docs = reader.readAll(Arrays.stream(Objects.requireNonNull(dir.listFiles())).toList());
            index.addDocuments(docs);

            BooleanExpressionParser parser = new BooleanExpressionParser();
            BooleanQuery simple = parser.parse(QUERY_SIMPLE);
            BooleanQuery complex = parser.parse(QUERY_COMPLEX);

            var result1 = simple.evaluate(index);
            var result2 = complex.evaluate(index);

            long end = System.currentTimeMillis();
            System.out.println("Total time (index): " + (end - start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            long start = System.currentTimeMillis();

            List<Document> docs = reader.readAll(Arrays.stream(Objects.requireNonNull(dir.listFiles())).toList());
            Corpus corpus = new Corpus();
            corpus.addDocuments(docs);
            IncidenceMatrix matrix = new IncidenceMatrix(corpus, new CoreNlpTokenizer());
            matrix.getIncidenceMatrix();

            long end = System.currentTimeMillis();
            System.out.println("Total time (matrix): " + (end - start) + "ms");

        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Finished!");
    }
}