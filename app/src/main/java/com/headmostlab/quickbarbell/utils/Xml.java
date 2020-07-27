package com.headmostlab.quickbarbell.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.StringWriter;
import java.math.BigDecimal;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by WOW on 28.12.2019.
 */
public class Xml {

    private static final String ARR_ELEMENT_NAME = "e";
    private Document doc;
    private Element root;
    private DocumentBuilder builder;

    @Inject
    public Xml(DocumentBuilder builder) {
        this.builder = builder;
        doc = builder.newDocument();
        root = doc.createElement("root");
        doc.appendChild(root);
    }

    public Xml(Document doc) {
        this.doc = doc;
        this.root = doc.getDocumentElement();
    }

    public void putInt(String name, int value) {
        putString(name, Integer.toString(value));
    }

    public void putBoolean(String name, boolean value) {
        putString(name, Boolean.toString(value));
    }

    public void putLong(String name, long value) {
        putString(name, Long.toString(value));
    }

    public void putFloat(String name, float value) {
        putString(name, Float.toString(value));
    }

    public void putBigDecimal(String name, BigDecimal value) {
        putString(name, value.toString());
    }

    public void putString(String elementName, String value) {
        Element e = doc.createElement(elementName);
        e.appendChild(doc.createTextNode(value));
        root.appendChild(e);
    }

    public void putLongs(String name, long[] longs) {
        String[] strings = new String[longs.length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = Long.toString(longs[i]);
        }
        putStrings(name, strings);
    }

    public void putFloats(String name, float[] floats) {
        String[] strings = new String[floats.length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = Float.toString(floats[i]);
        }
        putStrings(name, strings);
    }

    public void putBigDecimals(String name, BigDecimal[] bigDecimals) {
        String[] strings = new String[bigDecimals.length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = bigDecimals[i].toString();
        }
        putStrings(name, strings);
    }

    public void putStrings(String elementName, String[] values) {
        Element array = doc.createElement(elementName);
        for (String value : values) {
            final Element element = doc.createElement(ARR_ELEMENT_NAME);
            element.appendChild(doc.createTextNode(value));
            array.appendChild(element);
        }
        root.appendChild(array);
    }

    public Integer getInt(String name) {
        final String value = getString(name);
        return value == null ? null : Integer.parseInt(value);
    }

    public Boolean getBoolean(String name) {
        final String value = getString(name);
        return value == null ? null : Boolean.parseBoolean(value);
    }

    public Long getLong(String name) {
        final String value = getString(name);
        return value == null ? null : Long.parseLong(value);
    }

    public Float getFloat(String name) {
        final String value = getString(name);
        return value == null ? null : Float.parseFloat(value);
    }

    public BigDecimal getBigDecimal(String name) {
        final String value = getString(name);
        return value == null ? null : new BigDecimal(value);
    }

    public String getString(String elementName) {
        final NodeList elements = doc.getElementsByTagName(elementName);
        return elements.getLength() > 0 ? elements.item(0).getTextContent() : null;
    }

    public long[] getLongs(String elementName) {
        final String[] strings = getStrings(elementName);
        long[] longs = new long[strings.length];
        for (int i = 0; i < longs.length; i++) {
            longs[i] = Long.parseLong(strings[i]);
        }
        return longs;
    }

    public float[] getFloats(String elementName) {
        final String[] strings = getStrings(elementName);
        float[] floats = new float[strings.length];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = Float.parseFloat(strings[i]);
        }
        return floats;
    }

    public BigDecimal[] getBigDecimals(String elementName) {
        final String[] strings = getStrings(elementName);
        BigDecimal[] bigDecimals = new BigDecimal[strings.length];
        for (int i = 0; i < bigDecimals.length; i++) {
            bigDecimals[i] = new BigDecimal(strings[i]);
        }
        return bigDecimals;
    }

    public String[] getStrings(String elementName) {
        final NodeList arrayElement = doc.getElementsByTagName(elementName);
        if (arrayElement.getLength() == 0) {
            return null;
        }
        final NodeList valueElements = arrayElement.item(0).getChildNodes();
        String[] array = new String[valueElements.getLength()];
        for (int i = 0; i < array.length; i++) {
            array[i] = valueElements.item(i).getTextContent();
        }
        return array;
    }

    public String getString() throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }
}
