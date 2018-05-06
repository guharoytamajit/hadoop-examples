package com.pq.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pq.bean.recordtype.FilenameComponents;
import com.pq.bean.recordtype.RecordTypeConfig;
import com.pq.bean.recordtype.RecordTypeResponse;
import com.pq.bean.recordtype.XPathComponents;

public class RecordTypeConfigService {

	public static RecordTypeConfig constructRecordTypeConfig(String configJson)
			throws IOException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
		RecordTypeConfig recordType = new RecordTypeConfig();
		JsonNode root = mapper.readTree(configJson);

		String PRIORITY_FIELD = "Priority";
		String DEFAULT_FIELD = "DEFAULT";
		String FILENAME_FIELD = "Filename";
		String XPATH_FIELD = "XPATH";
		String PATTERN_FIELD = "Pattern";
		String VAL_FIELD = "Val";
		String XPATH = "Xpath";

		JsonNode priorityNode = root.path(PRIORITY_FIELD);
		for (JsonNode node : priorityNode) {
			recordType.getPriority().add(node.asText());
		}

		JsonNode defaultNode = root.path(DEFAULT_FIELD);
		recordType.setDefaultType(defaultNode.asText());

		JsonNode FilenameNode = root.path(FILENAME_FIELD);
		if (FilenameNode != null) {
			recordType.getProcessType().put(FILENAME_FIELD, new ArrayList<>());
			Iterator<Entry<String, JsonNode>> FilenameNodeFields = FilenameNode.fields();
			while (FilenameNodeFields.hasNext()) {
				FilenameComponents filenameComponents = new FilenameComponents();
				Entry<String, JsonNode> entry = FilenameNodeFields.next();
				filenameComponents.setRecType(entry.getKey());
				JsonNode jsonNode = entry.getValue();

				JsonNode patternNode = jsonNode.get(PATTERN_FIELD);

				for (JsonNode node : jsonNode.get(VAL_FIELD)) {
					filenameComponents.getPatterns().add(String.format(patternNode.asText(), node.asText()));
				}
				if(CollectionUtils.isEmpty(filenameComponents.getPatterns())) {
					filenameComponents.getPatterns().add(patternNode.asText());
				}

				recordType.getProcessType().get(FILENAME_FIELD).add(filenameComponents);
			}
		}

		JsonNode xpathNode = root.path(XPATH_FIELD);
		if (xpathNode != null) {
			recordType.getProcessType().put(XPATH_FIELD, new ArrayList<>());
			Iterator<Entry<String, JsonNode>> xpathNodeFields = xpathNode.fields();
			while (xpathNodeFields.hasNext()) {
				XPathComponents xpathComponents = new XPathComponents();
				Entry<String, JsonNode> entry = xpathNodeFields.next();
				xpathComponents.setRecType(entry.getKey());
				JsonNode jsonNode = entry.getValue();
				JsonNode commonNode = jsonNode.get(XPATH);
				for (JsonNode node : jsonNode.get(VAL_FIELD)) {
					xpathComponents.getXpathList().add(String.format(commonNode.asText(), node.asText()));
				}
				if(CollectionUtils.isEmpty(xpathComponents.getXpathList())) {
					xpathComponents.getXpathList().add(commonNode.asText());
				}
//				xpathComponents.getXpathList().add((jsonNode.get(XPATH).asText()));
				recordType.getProcessType().get(XPATH_FIELD).add(xpathComponents);
			}
		}
		return recordType;
	}

	public static RecordTypeConfig constructRecordTypeConfig2(String configJson)
			throws IOException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
		RecordTypeConfig recordType = new RecordTypeConfig();
		JsonNode root = mapper.readTree(configJson);
		

		String PRIORITY_FIELD = "priority";
		String DEFAULT_FIELD = "defaultType";
		String FILENAME_FIELD = "Filename";
		String XPATH_FIELD = "XPATH";
		String PATTERN_FIELD = "Pattern";
		String VAL_FIELD = "Val";
		String XPATH = "Xpath";
		String PROCESS_TYPE_FIELD = "processType";

		JsonNode priorityNode = root.path(PRIORITY_FIELD);
		for (JsonNode node : priorityNode) {
			recordType.getPriority().add(node.asText());
		}

		JsonNode defaultNode = root.path(DEFAULT_FIELD);
		recordType.setDefaultType(defaultNode.asText());
		
		
		JsonNode processTypeNode = root.path(PROCESS_TYPE_FIELD);
		JsonNode fileNameNode = processTypeNode.path(FILENAME_FIELD);
		
		if (fileNameNode != null) {
			recordType.getProcessType().put(FILENAME_FIELD, new ArrayList<>());
			for (JsonNode node : fileNameNode) {
				FilenameComponents filenameComponents = new FilenameComponents();
				JsonNode patternNode = node.path("patterns");
				for (JsonNode pattern : patternNode) {
					filenameComponents.getPatterns().add(pattern.asText());
				}
				filenameComponents.setRecType(node.path("recType").asText());
				recordType.getProcessType().get(FILENAME_FIELD).add(filenameComponents);
			}
		}
		
		JsonNode xpathNode = processTypeNode.path(XPATH_FIELD);
		if (xpathNode != null) {
			recordType.getProcessType().put(XPATH_FIELD, new ArrayList<>());
			for (JsonNode node : xpathNode) {
				XPathComponents xpathComponents = new XPathComponents();
				JsonNode xpathListNode = node.path("xpathList");
				for (JsonNode xpath : xpathListNode) {
					xpathComponents.getXpathList().add(xpath.asText());
				}
				xpathComponents.setRecType(node.path("recType").asText());
				recordType.getProcessType().get(XPATH_FIELD).add(xpathComponents);
			}
		}		
		return recordType;
	}
	
	public static RecordTypeResponse detectRecordType(RecordTypeConfig recordTypeConfig, String fileName, String record,
			String detectionType) {
		String recType = null;
		Map<String, List<Object>> processTypes = recordTypeConfig.getProcessType();
		String FILENAME = "Filename";
		String XPATH = "XPATH";
		String DEFAULT="DEFAULT";
		if (detectionType == null) {
			List<String> priorityList = recordTypeConfig.getPriority();
			for (String priority : priorityList) {
				if (priority.equals(FILENAME)) {
					recType = detectRecordTypeFromFilename(processTypes.get(priority), fileName);
					if (recType != null)
						return new RecordTypeResponse(recType, FILENAME);
				} else if (priority.equals(XPATH)) {
					recType = detectRecordTypeFromXpath(processTypes.get(priority), record);
					if (recType != null)
						return new RecordTypeResponse(recType, XPATH);
				}
			}
		} else if (detectionType.equals(FILENAME)) {
			recType = detectRecordTypeFromFilename(processTypes.get(FILENAME), fileName);
			if (recType != null)
				return new RecordTypeResponse(recType, FILENAME);
		} else if (detectionType.equals(XPATH)||detectionType.equals(DEFAULT)) {
			recType = detectRecordTypeFromXpath(processTypes.get(XPATH), record);
			if (recType != null)
				return new RecordTypeResponse(recType, XPATH);
		}

		return new RecordTypeResponse(recordTypeConfig.getDefaultType(), DEFAULT);
	}

	public static String detectRecordTypeFromFilename(List<Object> list, String fileNme) {
		String recordType = null;

		for (Object obj : list) {
			FilenameComponents filenameComponents = (FilenameComponents) obj;
			List<String> patternList = filenameComponents.getPatterns();
			for (String pattern : patternList) {
				boolean patternMatchFound = Pattern.compile(pattern).matcher(fileNme).matches();
				if (patternMatchFound) {
					return filenameComponents.getRecType();
				}
			}
		}

		return recordType;
	}

	private static String detectRecordTypeFromXpath(List<Object> list, String record) {
		String recordType = null;
		for (Object obj : list) {
			XPathComponents xPathComponents = (XPathComponents) obj;
			List<String> xpathList = xPathComponents.getXpathList();
			for (String path : xpathList) {
				boolean xpathMatchFound = xpathMatchFound(record, path);
				if (xpathMatchFound) {
					return xPathComponents.getRecType();
				}
			}
		}
		return recordType;
	}

	public static boolean xpathMatchFound(String record, String xpathStr) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document doc;
		Node node = null;
		try {
			doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(record)));
			XPath xpath = XPathFactory.newInstance().newXPath();
			node = (Node) xpath.evaluate(xpathStr, doc, XPathConstants.NODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (node != null) {
			return true;
		}
		return false;
	}
}
