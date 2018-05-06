package com.pq.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordCount {
	int add;
	int aup;
	int aud;
	int del;
	int upd;
	String size;

	public int getAdd() {
		return add;
	}

	public void setAdd(int add) {
		this.add = add;
	}

	public int getDel() {
		return del;
	}

	public void setDel(int del) {
		this.del = del;
	}

	public int getTotal() {
		return add + aup + aud + del+upd;
	}

	public void aupIncrement() {
		aup = aup + 1;
	}

	public void delIncrement() {
		del = del + 1;
	}
	public void addIncrement() {
		add = add + 1;
	}
	public void updIncrement() {
		upd = upd + 1;
	}
	public void audIncrement() {
		aud = aud + 1;
	}
	

	@Override
	public String toString() {
		return add + "," + aud + "," + aup + "," + del + "," + upd+ "," + size;
	}

	public int getAup() {
		return aup;
	}

	public void setAup(int aup) {
		this.aup = aup;
	}

	public int getAud() {
		return aud;
	}

	public void setAud(int aud) {
		this.aud = aud;
	}

	public int getUpd() {
		return upd;
	}

	public void setUpd(int upd) {
		this.upd = upd;
	}

	public RecordCount() {
	}
	
	public RecordCount(String csv) {
		String[] splits = csv.split(",");
		try {
		add=Integer.parseInt(splits[0]);
		aud=Integer.parseInt(splits[1]);
		aup=Integer.parseInt(splits[2]);
		del=Integer.parseInt(splits[3]);
		upd=Integer.parseInt(splits[4]);
		size=splits[5];
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	public static RecordCount merge(RecordCount recordCount1,RecordCount recordCount2) {
		RecordCount recordCount=new RecordCount();
		recordCount.setAdd(recordCount1.getAdd()+recordCount2.getAdd());
		recordCount.setAud(recordCount1.getAud()+recordCount2.getAud());
		recordCount.setAup(recordCount1.getAup()+recordCount2.getAup());
		recordCount.setDel(recordCount1.getDel()+recordCount2.getDel());
//		recordCount.setSize(recordCount1.getAdd()+recordCount2.getAdd());
		recordCount.setUpd(recordCount1.getUpd()+recordCount2.getUpd());
		return recordCount;
	}
}
