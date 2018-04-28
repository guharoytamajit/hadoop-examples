package com;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.hadoop.io.WritableComparable;

import com.google.common.collect.ComparisonChain;

public class MyKey implements WritableComparable<MyKey> {
	
	String filename;
	Long offset;

	@Override
	public void readFields(DataInput input) throws IOException {
		filename=input.readUTF();
		offset=input.readLong();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(filename);
		output.writeLong(offset);
	}

	@Override
	public int compareTo(MyKey obj) {
		// TODO Auto-generated method stub
		return ComparisonChain.start().compare(this.offset, obj.offset).result();
	}

}
