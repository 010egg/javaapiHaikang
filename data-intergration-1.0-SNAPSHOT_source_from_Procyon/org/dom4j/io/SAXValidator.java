// 
// Decompiled by Procyon v0.5.36
// 

package org.dom4j.io;

import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.dom4j.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;

public class SAXValidator
{
    private XMLReader xmlReader;
    private ErrorHandler errorHandler;
    
    public SAXValidator() {
    }
    
    public SAXValidator(final XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }
    
    public void validate(final Document document) throws SAXException {
        if (document != null) {
            final XMLReader reader = this.getXMLReader();
            if (this.errorHandler != null) {
                reader.setErrorHandler(this.errorHandler);
            }
            try {
                reader.parse(new DocumentInputSource(document));
            }
            catch (IOException e) {
                throw new RuntimeException("Caught and exception that should never happen: " + e);
            }
        }
    }
    
    public XMLReader getXMLReader() throws SAXException {
        if (this.xmlReader == null) {
            this.xmlReader = this.createXMLReader();
            this.configureReader();
        }
        return this.xmlReader;
    }
    
    public void setXMLReader(final XMLReader reader) throws SAXException {
        this.xmlReader = reader;
        this.configureReader();
    }
    
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    protected XMLReader createXMLReader() throws SAXException {
        return SAXHelper.createXMLReader(true);
    }
    
    protected void configureReader() throws SAXException {
        final ContentHandler handler = this.xmlReader.getContentHandler();
        if (handler == null) {
            this.xmlReader.setContentHandler(new DefaultHandler());
        }
        this.xmlReader.setFeature("http://xml.org/sax/features/validation", true);
        this.xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        this.xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
    }
}
