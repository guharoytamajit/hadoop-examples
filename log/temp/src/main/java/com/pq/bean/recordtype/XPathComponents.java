package com.pq.bean.recordtype;

import java.util.ArrayList;
import java.util.List;

public class XPathComponents {

	private List<String> xpathList;
	private String recType;


	public List<String> getXpathList() {
		if(xpathList==null) {
			xpathList=new ArrayList<>();
		}
		return xpathList;
	}
	public void setXpathList(List<String> xpathList) {
		this.xpathList = xpathList;
	}
	public String getRecType() {
		return recType;
	}
	public void setRecType(String recType) {
		this.recType = recType;
	}
	@Override
	public String toString() {
		return "XPathComponents [xpathList=" + xpathList + ", recType=" + recType + "]";
	}

}
