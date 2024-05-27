package org.kma;

import org.kma.document.Corpus;
import org.kma.document.Document;
import org.kma.document.reading.TxtDocumentReader;
import org.kma.index.InvertedIndex;
import org.kma.processing.CoreNlpTokenizer;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        TxtDocumentReader reader = new TxtDocumentReader();
        InvertedIndex index = new InvertedIndex(new Corpus(), new CoreNlpTokenizer());

        File dir = new File("src/main/resources/documents");
        try {
            List<Document> docs = reader.readAll(Arrays.stream(Objects.requireNonNull(dir.listFiles())).toList());
            index.addDocuments(docs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished!");
    }
}