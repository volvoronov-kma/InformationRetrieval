package org.kma.document.reading;

import org.kma.document.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface DocumentReader {

    public Document read(File file) throws IOException;

    public List<Document> readAll(List<File> files) throws IOException;

}
