package com.headmostlab.quickbarbell.di;

import com.headmostlab.quickbarbell.utils.Xml;
import com.headmostlab.quickbarbell.utils.XmlParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import dagger.Module;
import dagger.Provides;

@Module
public interface XmlModule {
    @ApplicationScope
    @Provides
    static DocumentBuilder provideXmlBuilder() {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return builder;
    }

    @ApplicationScope
    @Provides
    static XmlParser provideXmlParser(DocumentBuilder builder) {
        return new XmlParser(builder);
    }

    @Provides
    static Xml provideXml(DocumentBuilder builder) {
        return new Xml(builder);
    }
}
