package conf;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;

public class GenericOptionsParserExample {
	public static void main(String[] args) throws IOException {
		Configuration configuration = new Configuration();
		configuration.addDefaultResource("myConfig-default.xml");
		configuration.addResource("myConfig-site.xml");
		//this can be overwritten by passing -D myname=tamajit as args
		configuration.set("myname", "abc");
		org.apache.hadoop.util.GenericOptionsParser parser = new org.apache.hadoop.util.GenericOptionsParser(
				configuration, args);
		// remaining arguments are args which comes after -D key=value
		System.out.println("Remaining arguments: " + Arrays.asList(parser.getRemainingArgs()));
		System.out.println(parser.getConfiguration().get("color"));
		System.out.println(parser.getConfiguration().get("fruit"));
		// pass -D myname=tamajit as args to overwrite default value
		System.out.println(parser.getConfiguration().get("myname"));
	}
}
