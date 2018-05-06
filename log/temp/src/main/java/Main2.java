import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pq.bean.recordtype.FilenameComponents;
import com.pq.bean.recordtype.RecordTypeConfig;
import com.pq.bean.recordtype.XPathComponents;
import com.pq.util.XmlValidationUtil;

public class Main2 {
	public static void main(String[] args) throws Exception {

		// CloseableHttpClient client = HttpClients.createDefault();
		// HttpPost httpPost = new
		// HttpPost("https://rod5b1fbh9.execute-api.us-east-1.amazonaws.com/test/");
		// String json = "{\"CONFIG_NAME\":\"India=IP\",\"BATCHID\":\"TESTINDIA\"}";
		// StringEntity entity = new StringEntity(json);
		// httpPost.setEntity(entity);
		// httpPost.setHeader("X-API-KEY", "fK0HHx97gU1L1Edk0iHSo6IyYWFrAVhV80aBGfff");
		// httpPost.setHeader("Content-type", "application/json");
		// CloseableHttpResponse response = client.execute(httpPost);

		/*
		 * BufferedReader br = new BufferedReader(new InputStreamReader(new
		 * FileInputStream("emp.xml"))); String line; String startTag="<emp"; String
		 * endTag="</emp"; StringBuffer record=new StringBuffer(); while ((line =
		 * br.readLine()) != null) { System.out.println(line); } br.close();
		 * System.out.println("done....");
		 */

		/*
		 * Properties prop = new Properties(); InputStream input = null; String filepath
		 * = "src/main/resources/workflow.properties"; File file = new File(filepath);
		 * input = new FileInputStream(file); // load a properties file
		 * prop.load(input); String mappers =(String) prop.get("MEDLINE.mappers");
		 * System.out.println(mappers); String[] mapperDetails= mappers.split("\\|");
		 * for(String mapper:mapperDetails) {
		 * 
		 * String[] split = mapper.split(","); System.out.println(split); }
		 */

		/*
		 * Pattern r = Pattern.compile("medline(.*?).xml.zip",
		 * Pattern.CASE_INSENSITIVE); Matcher matcher =
		 * r.matcher("medline17n1184.xml.zip"); // check all occurance while
		 * (matcher.find()) { // System.out.print("Start index: " + matcher.start()); //
		 * System.out.print(" End index: " + matcher.end() + " ");
		 * System.out.println(matcher.group(1)); }
		 */

		// Path normFilePath = new
		// Path("/usr/test/norm/BIOSISPREVIEWS/BIOSISPREVIEWS-IP_SS_2017-10-09T22-02-52/BIOSISPREVIEWS-1618.ADD.2018-02-08T02-33-44Z.ERR");
		// FileSystem fs = FileSystem.get(URI.create(normFilePath.toString()), new
		// Configuration());
		// Path dtdPath = new
		// Path("/usr/test/norm/BIOSISPREVIEWS/BIOSISPREVIEWS-IP_SS_2017-10-09T22-02-52/BiosisPreviews.dtd");
		// boolean normValid =
		// XmlValidationUtil.validateXmlAgainstExternalDTD(fs.open(normFilePath),fs.open(dtdPath));
		// System.out.println(normValid);

		/*
		 * SchemaFactory schemaFactory = SchemaFactory
		 * .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); Schema schema =
		 * schemaFactory.newSchema(new File(
		 * "/usr/test/norm/BIOSISPREVIEWS/BIOSISPREVIEWS-IP_SS_2017-10-09T22-02-52/BiosisPreviews.dtd"
		 * )); Validator validator = schema.newValidator(); validator.validate(new
		 * StreamSource(
		 * "/usr/test/norm/BIOSISPREVIEWS/BIOSISPREVIEWS-IP_SS_2017-10-09T22-02-52/PREV019018.xml"
		 * ));
		 */

		RecordTypeConfig recordTypeConfig = constructRecordTypeConfig();

		RecordTypeResponse res = detectRecordType(recordTypeConfig, "", "", null);
		System.out.println(res.getRecordType());

	}

	private static RecordTypeResponse detectRecordType(RecordTypeConfig recordType, String fileName, String record,
			String detectionType) {
		String recType = null;
		Map<String, List<Object>> processTypes = recordType.getProcessType();
		String FILENAME = "Filename";
		String XPATH = "XPATH";
		if (detectionType == null) {
			List<String> priorityList = recordType.getPriority();
			for (String priority : priorityList) {
				if (priority.equals(FILENAME)) {
					recType = detectRecordTypeFromFilename(processTypes.get(priority), fileName);
					if (recType != null)
						return new RecordTypeResponse(recType, FILENAME);
				} else if (priority.equals( XPATH)) {
					recType = detectRecordTypeFromXpath(processTypes.get(priority), record);
					if (recType != null)
						return new RecordTypeResponse(recType, XPATH);
				}
			}
		} else {
			if (detectionType.equals(FILENAME)) {
				recType = detectRecordTypeFromFilename(processTypes.get(detectionType), fileName);
				return new RecordTypeResponse(recType, FILENAME);
			} else if (detectionType.equals( XPATH)) {
				recType = detectRecordTypeFromXpath(processTypes.get(detectionType), record);
				return new RecordTypeResponse(recType, XPATH);
			}
		}

		return null;
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

	public static RecordTypeConfig constructRecordTypeConfig() throws IOException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		RecordTypeConfig recordType = new RecordTypeConfig();
		JsonNode root = mapper.readTree(new File("/home/tamajit/Desktop/recordtype.json"));

		JsonNode priorityNode = root.path("Priority");
		for (JsonNode node : priorityNode) {
			recordType.getPriority().add(node.asText());
		}

		JsonNode defaultNode = root.path("DEFAULT");
		recordType.setDefaultType(defaultNode.asText());

		JsonNode FilenameNode = root.path("Filename");
		if (FilenameNode != null) {
			recordType.getProcessType().put("Filename", new ArrayList<>());
			Iterator<Entry<String, JsonNode>> FilenameNodeFields = FilenameNode.fields();
			while (FilenameNodeFields.hasNext()) {
				FilenameComponents filenameComponents = new FilenameComponents();
				Entry<String, JsonNode> entry = FilenameNodeFields.next();
				filenameComponents.setRecType(entry.getKey());
				JsonNode jsonNode = entry.getValue();
				JsonNode patternNode = jsonNode.get("Pattern");
				 for (JsonNode node : jsonNode.get("Val")) {
					 filenameComponents.getPatterns().add(String.format(patternNode.asText(),node.asText()));
				 }

				recordType.getProcessType().get("Filename").add(filenameComponents);
			}
		}

		JsonNode xpathNode = root.path("XPATH");
		if (xpathNode != null) {
			recordType.getProcessType().put("XPATH", new ArrayList<>());
			Iterator<Entry<String, JsonNode>> xpathNodeFields = xpathNode.fields();
			while (xpathNodeFields.hasNext()) {
				XPathComponents xpathComponents = new XPathComponents();
				Entry<String, JsonNode> entry = xpathNodeFields.next();
				xpathComponents.setRecType(entry.getKey());
				JsonNode jsonNode = entry.getValue();
				// for (JsonNode node : jsonNode) {
				// xpathComponents.getXpath().add(node.asText());
				// }
				xpathComponents.getXpathList().add((jsonNode.get("Xpath").asText()));
				recordType.getProcessType().get("XPATH").add(xpathComponents);
			}
		}
		return recordType;
	}
}

class RecordTypeResponse{
	String recordType;
	String recordDetectionType;
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getRecordDetectionType() {
		return recordDetectionType;
	}
	public void setRecordDetectionType(String recordDetectionType) {
		this.recordDetectionType = recordDetectionType;
	}
	public RecordTypeResponse(String recordType, String recordDetectionType) {
		super();
		this.recordType = recordType;
		this.recordDetectionType = recordDetectionType;
	}
	
}
