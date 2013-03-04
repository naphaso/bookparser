package com.undefware.bookparser;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.stax2.XMLStreamReader2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: wolong
 * Date: 3/4/13
 * Time: 10:37 AM
 */
public class BookParser implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BookParser.class);
    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private File file;

    public BookParser(File file) {
        this.file = file;
    }


    @Override
    public void run() {
        logger.debug("parsing file {}", file.getAbsolutePath());
        if(!file.exists() || !file.isFile() || !file.canRead())
            return;

        String ext = FilenameUtils.getExtension(file.getAbsolutePath());

        InputStream inputStream = null;

        if("zip".equals(ext)) {
            try {
                ZipFile zipFile = new ZipFile(file, Charset.forName("CP866"));
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                ZipEntry zipEntry = entries.nextElement();

                inputStream = zipFile.getInputStream(zipEntry);
            } catch (IOException e) {
                logger.error("error", e);
            }
        } else if("fb2".equals(ext)) {
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                logger.error("error", e);
            }
        }

        if(inputStream == null) {
            logger.error("file not opened: {}", file.getAbsolutePath());
            return;
        }

        process(inputStream);

        try {
            inputStream.close();
        } catch (IOException e) {
            logger.error("error", e);
        }
    }

    private void process(InputStream inputStream) {

        try {
            XMLStreamReader2 xmlReader = (XMLStreamReader2) xmlInputFactory.createXMLStreamReader(inputStream);

            while(xmlReader.hasNext()) {
                int type = xmlReader.next();
                if(type == XMLStreamConstants.START_ELEMENT) {
                    if(xmlReader.getLocalName().equals("description")) {
                        processDescription(xmlReader);
                    } else if(xmlReader.getLocalName().equals("p")) {
                        processParagraph(xmlReader);
                    }

                }
            }

        } catch (XMLStreamException e) {
            logger.warn("xml stream error", e);
        }

        //logger.info("successfully opened file: {}", file.getAbsolutePath());
    }

    private void processParagraph(XMLStreamReader2 xmlReader) throws XMLStreamException {
        while(xmlReader.hasNext()) {
            int type = xmlReader.next();
            if(type == XMLStreamConstants.END_ELEMENT)
                if(xmlReader.getLocalName().equals("p"))
                    return;
            if(type == XMLStreamConstants.CHARACTERS) {
                String text = xmlReader.getText();
                logger.debug("paragraph: {}", text);
            }
        }
    }

    private void processDescription(XMLStreamReader2 xmlReader) throws XMLStreamException {
        while(xmlReader.hasNext()) {
            int type = xmlReader.next();
            if(type == XMLStreamConstants.START_ELEMENT) {
                logger.debug("start description element: {}", xmlReader.getLocalName());
            }
            if(type == XMLStreamConstants.CHARACTERS) {
                logger.debug("description text: {}", xmlReader.getText());
            }
            if(type == XMLStreamConstants.END_ELEMENT) {
                logger.debug("end description element: {}", xmlReader.getLocalName());
                if(xmlReader.getLocalName().equals("description"))
                    return;
            }
        }
    }
}
