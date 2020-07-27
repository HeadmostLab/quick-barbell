package com.headmostlab.quickbarbell.testutils;

import com.headmostlab.quickbarbell.utils.Xml;
import com.headmostlab.quickbarbell.utils.XmlParser;

import javax.inject.Provider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlUtils {

    private static DocumentBuilder xmlBuilder;

    public static DocumentBuilder provideXmlBuilder() {
        if (xmlBuilder != null) {
            return xmlBuilder;
        }
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            xmlBuilder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return xmlBuilder;
    }

    public static Provider<Xml> getXmlProvider() {
        return () -> new Xml(provideXmlBuilder());
    }

    public static XmlParser getXmlParser() {
        return new XmlParser(provideXmlBuilder());
    }
}
