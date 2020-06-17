package org.example;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;

public class StaxEventProcessor implements AutoCloseable {

    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

    private final XMLEventReader reader;
    private XMLEvent event;

    public StaxEventProcessor(InputStream is) throws XMLStreamException {
        reader = FACTORY.createXMLEventReader(is);
    }

    public XMLEvent getEvent() {
        return event;
    }

    public boolean next() {
        if (!reader.hasNext()) {
            return false;
        }
        try {
            event = reader.nextEvent();
            return true;
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isStartElement(String value) throws XMLStreamException {
        return event.isStartElement() && value.equals(event.asStartElement().getName().getLocalPart());
    }

    public boolean isEndElement(String value) throws XMLStreamException {
        return event.isEndElement() && value.equals(event.asEndElement().getName().getLocalPart());
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) { // empty
            }
        }
    }
}
