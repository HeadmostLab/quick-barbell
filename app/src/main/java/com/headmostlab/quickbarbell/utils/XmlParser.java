package com.headmostlab.quickbarbell.utils;


import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;

public class XmlParser {

    private DocumentBuilder builder;

    @Inject
    public XmlParser(DocumentBuilder builder) {
        this.builder = builder;
    }

    public Xml parse (String xml) throws IOException, SAXException {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8")));
        return new Xml(builder.parse(inputStream));
    }
}
