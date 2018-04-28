package com;

import org.apache.hadoop.mapreduce.Partitioner;

/** Partition keys by their {@link Object#hashCode()}. */
public class WCPartitioner<K, V> extends Partitioner<K, V> {

  /** Use {@link Object#hashCode()} to partition. */
  public int getPartition(K key, V value,
                          int numReduceTasks) {
	 int partition = (key.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
	 if(key.toString().equalsIgnoreCase("good")) {
		 partition=3;
	 }
	 
	 System.out.println(key+" going to partition "+partition);
	  
    return partition;
  }

}
