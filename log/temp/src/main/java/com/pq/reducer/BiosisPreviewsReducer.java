package com.pq.reducer;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.pq.bean.ResponseToReducer;

public class BiosisPreviewsReducer extends GenericProcessReducer{
	
/*	public StringBuilder generateNormFileName(String dbName,ResponseToReducer responseToReducer,String recordType) {
		StringBuilder normFileBuild = new StringBuilder();
		normFileBuild.append(dbName);
		normFileBuild.append("-");
		normFileBuild.append(responseToReducer.getSequenceNumber());
		normFileBuild.append("."+recordType+".");
		normFileBuild.append(responseToReducer.getCurrentFileModDateTime());
		normFileBuild.append("Z");
		return normFileBuild;
	}*/

/*	@Override
	public String detectRecordType(String record) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document doc;
		Node updateNode = null; 
		try {
			doc = factory.newDocumentBuilder()
						.parse(new InputSource(new StringReader(record)));
			XPath xpath = XPathFactory.newInstance().newXPath();
			 updateNode = (Node)xpath.evaluate(conf.get("UPD.detector"), doc, XPathConstants.NODE);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if(updateNode!=null) {
			return "UPD";
		}
			return "ADD";
	}*/

}
