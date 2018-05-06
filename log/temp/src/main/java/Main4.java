import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.pq.util.XmlValidationUtil;

public class Main4 {
	public static void main(String[] args) throws Exception {
		/*boolean validateXmlAgainstExternalDTD = XmlValidationUtil.validateXmlAgainstExternalDTD(
				new FileInputStream("/usr/test/drop/BIOSISPREVIEWS-IP_SS_2017-10-09T22-02-52/prev1618/PREV019018.xml"),
				new FileInputStream("/usr/test/drop/BIOSISPREVIEWS-IP_SS_2017-10-09T22-02-52/BIOSISPreviews.dtd"));*/
		// boolean validateXmlAgainstExternalDTD =
		// XmlValidationUtil.validateXmlAgainstExternalDTD(new
		// FileInputStream("/usr/test/drop/BIOSISPREVIEWS-IP_SS_2017-10-09T22-02-52/prev1618/test.xml"),
		// new
		// FileInputStream("/usr/test/drop/BIOSISPREVIEWS-IP_SS_2017-10-09T22-02-52/test.dtd"));

		boolean validateXmlAgainstExternalDTD = XmlValidationUtil.validateXmlWithDtd(
				new FileInputStream("/usr/test/drop/BIOSISPREVIEWS-IP_SS_2017-10-09T22-02-52/prev1618/PREV019018.xml"));
		System.out.println(validateXmlAgainstExternalDTD);
	}
}
