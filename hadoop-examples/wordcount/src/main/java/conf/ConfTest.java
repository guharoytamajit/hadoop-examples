package conf;

import java.util.Map;

import org.apache.hadoop.conf.Configuration;

public class ConfTest {
	public static void main(String[] args) {
		Configuration.addDefaultResource("myConfig-default.xml");//default for all configurations
		Configuration config1 = new Configuration();
		config1.addResource("myConfig-site.xml");//this will override defaults
		for (Map.Entry<String, String> entry : config1) {
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
		System.out.println("===========================================");
		Configuration config2 = new Configuration();
		System.out.println("config1 color="+config1.get("color"));
		System.out.println("config2 color="+config2.get("color"));
		
	}
}