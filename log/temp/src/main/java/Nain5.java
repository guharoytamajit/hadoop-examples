import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class Nain5 {
public static void main(String[] args) throws Exception {
String str="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + 
		"<!DOCTYPE BIOSISCitationSet PUBLIC '-//BIOSIS//Previews DTD'"
		+ " 'http://www.biosis.org/dtd/BIOSISPreviews.dtd'>\n" + 
		"<BIOSISCitationSet>";
str = str.replaceAll("(?<=<!DOCTYPE).*?(?=>)", "replacement");
System.out.println(str);


}
}
