package conf;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;

public class GenericOptionsParserExample {
public static void main(String[] args) throws IOException {
	org.apache.hadoop.util.GenericOptionsParser parser = new org.apache.hadoop.util.GenericOptionsParser(new Configuration(),args);
	System.out.println("Remaining arguments: "+Arrays.asList(parser.getRemainingArgs()));
	System.out.println(parser.getConfiguration().get("color"));
}
}
