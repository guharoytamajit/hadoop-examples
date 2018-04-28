package com;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;

public class MyPartitioner extends Partitioner<Text, Text> implements Configurable{

	Configuration conf;

	@Override
	public int getPartition(Text key, Text value, int num) {
		Properties properties=new Properties();

		try {
			URI[] cacheFiles = Job.getInstance(getConf()).getCacheFiles();
			for (URI uri : cacheFiles) {
				if(uri.toString().equalsIgnoreCase("execution_plan"))
				{
					properties.load(new java.io.FileReader(uri.getPath().toString()));
//					 java.io.BufferedReader br =new java.io.BufferedReader(new java.io.FileReader(uri.getPath().toString()));
//				     String pattern;
//					while (( pattern = br.readLine()) != null) {
//				         System.out.println(pattern);
//				       }
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return Integer.parseInt(properties.getProperty(key.toString(),"0"));
	}

	@Override
	public Configuration getConf() {
		// TODO Auto-generated method stub
		return conf;
	}

	@Override
	public void setConf(Configuration arg0) {
		conf=arg0;
		
	}




	
	
}
