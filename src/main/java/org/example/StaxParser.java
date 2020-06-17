package org.example;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaxParser {

    public void parse(Path path) {
        try (StaxEventProcessor processor = new StaxEventProcessor(Files.newInputStream(path))) {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            StringWriter stringWriter = new StringWriter();
            XMLEventWriter xmlEventWriter = outputFactory.createXMLEventWriter(stringWriter);
            int shldCount = 0;
            int limit = 30000;
            int total = 0;
            boolean inShareholder = false;
            while (processor.next()) {
                if (processor.isStartElement("shareholder")) {
                    inShareholder = true;
                } else if (processor.isEndElement("shareholder")) {
                    inShareholder = false;
                    xmlEventWriter.add(processor.getEvent());
                    shldCount++;
                }
                if (inShareholder) {
                    xmlEventWriter.add(processor.getEvent());
                }
                if (shldCount >= limit) {
                    xmlEventWriter.flush();
                    total += shldCount;
                    shldCount = 0;
                    Files.write(path.resolveSibling("file_" + total + ".xml"), stringWriter.toString().getBytes());
                    stringWriter.getBuffer().setLength(0);
                }
            }
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
