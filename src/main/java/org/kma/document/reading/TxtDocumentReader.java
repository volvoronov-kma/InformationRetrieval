package org.kma.document.reading;

import org.kma.document.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TxtDocumentReader implements DocumentReader {
    @Override
    public Document read(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        return new Document(lines);
    }

    @Override
    public List<Document> readAll(List<File> files) throws IOException {
        List<Document> documents = new ArrayList<>();
        for (File file : files) {
            documents.add(read(file));
        }
        return documents;
    }
}
