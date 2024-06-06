package org.kma;

import org.kma.document.Corpus;
import org.kma.document.Document;
import org.kma.document.reading.TxtDocumentReader;
import org.kma.index.IncidenceMatrix;
import org.kma.index.InvertedIndex;
import org.kma.index.domain.NGramOrder;
import org.kma.processing.BiWordTokenizer;
import org.kma.processing.CoreNlpTokenizer;
import org.kma.search.parser.BooleanExpressionParser;
import org.kma.search.query.BooleanQuery;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
//        "Here comes the phrase" - phrasal search is invoked via putting a request in quotes
        String QUERY_SIMPLE = "\"Whose worst was that the noble Mortimer\"";
        String QUERY_COMPLEX = "\"To be or not to be\"";






        TxtDocumentReader reader = new TxtDocumentReader();

        File dir = new File("src/main/resources/documents");

        try {
            BooleanExpressionParser parser = new BooleanExpressionParser(BooleanExpressionParser.ParserType.POSITIONAL);
            BooleanQuery simple = parser.parse(QUERY_SIMPLE);
            BooleanQuery complex = parser.parse(QUERY_COMPLEX);

            long start = System.currentTimeMillis();

            List<Document> docs = reader.readAll(Arrays.stream(Objects.requireNonNull(dir.listFiles())).toList());
            InvertedIndex index = new InvertedIndex(new Corpus(), new CoreNlpTokenizer());
            index.addDocuments(docs);

            var simpleResult = simple.evaluate(index);
            var complexResult = complex.evaluate(index);

            List<String> simpleNames = new ArrayList<>();
            List<String> complexNames = new ArrayList<>();

            simpleResult.forEach(
                    result -> simpleNames.add(index.getCorpus().getDocument(result).getTitle())
            );
            complexResult.forEach(
                    result -> complexNames.add(index.getCorpus().getDocument(result).getTitle())
            );

            long end = System.currentTimeMillis();
            System.out.println("Positional Index Simple Query " + simpleNames);
            System.out.println("Positional Index Complex Query " + complexNames);
            System.out.println("Total time (index): " + (end - start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BooleanExpressionParser parser = new BooleanExpressionParser(BooleanExpressionParser.ParserType.NGRAM);
            parser.setnGramOrder(NGramOrder.BIGRAM);
            BooleanQuery simple = parser.parse(QUERY_SIMPLE);
            BooleanQuery complex = parser.parse(QUERY_COMPLEX);

            long start = System.currentTimeMillis();

            List<Document> docs = reader.readAll(Arrays.stream(Objects.requireNonNull(dir.listFiles())).toList());
            InvertedIndex index = new InvertedIndex(new Corpus(), new BiWordTokenizer());
            index.addDocuments(docs);

            var simpleResult = simple.evaluate(index);
            var complexResult = complex.evaluate(index);

            List<String> simpleNames = new ArrayList<>();
            List<String> complexNames = new ArrayList<>();

            simpleResult.forEach(
                    result -> simpleNames.add(index.getCorpus().getDocument(result).getTitle())
            );
            complexResult.forEach(
                    result -> complexNames.add(index.getCorpus().getDocument(result).getTitle())
            );

            long end = System.currentTimeMillis();
            System.out.println("Biword Index Simple Query " + simpleNames);
            System.out.println("Biword Index Complex Query " + complexNames);
            System.out.println("Total time (index): " + (end - start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Finished!");
    }
}