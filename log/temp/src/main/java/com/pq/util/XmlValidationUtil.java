package com.pq.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class XmlValidationUtil {
	private static  Logger log = Logger.getLogger(XmlValidationUtil.class);

	public static  boolean validateXmlWithDtd(InputStream xml) throws IOException {
		log.info("Inside validateXmlWithDtd() of XmlValidation...");

		boolean status = false;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			

			SAXParser parser = factory.newSAXParser();

			XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(new ErrorHandler() {
				@Override
				public void warning(SAXParseException e) throws SAXException {
					log.warn(e.getMessage()); // do nothing
				}

				@Override
				public void error(SAXParseException e) throws SAXException {
					log.error(e.getMessage());
					throw e;
				}

				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					log.fatal(e.getMessage());
					throw e;
				}
			});
			reader.parse(new InputSource(xml));
			status = true;
		} catch (ParserConfigurationException ex) {
			log.error("ParserConfigurationException: " + ex);

		} catch (SAXException ex) {
			log.error("SAXException: " + ex);
		} catch (IOException ex) {
			log.error("IOException: " + ex);
		}

		return status;
	}
	
	public static boolean validateXmlAgainstExternalDTD(InputStream xml, InputStream dtd){
		boolean status = false;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setValidating(true);
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, dtd);
		
		Document doc = null;
		try {
			DocumentBuilder parser = factory.newDocumentBuilder();
			doc = parser.parse(xml);
			status = true;
		} catch (ParserConfigurationException e) {
			System.out.println("Parser not configured: " + e.getMessage());
		} catch (SAXException e) {
			System.out.print("Parsing XML failed due to a " + e.getClass().getName() + ":");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return status;
	}
	
//	public static boolean validateXmlAgainstExternalDTD(InputStream xml, InputStream dtd){
//        boolean status = false;
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//
//        factory.setValidating(true);
//        
//        Document doc = null;
//        try {
//            DocumentBuilder parser = factory.newDocumentBuilder();
//            doc = parser.parse(xml);
//            status = true;
//        } catch (ParserConfigurationException e) {
//            System.out.println("Parser not configured: " + e.getMessage());
//        } catch (SAXException e) {
//            System.out.print("Parsing XML failed due to a " + e.getClass().getName() + ":");
//            System.out.println(e.getMessage());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return status;
//    }


}
