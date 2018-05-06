package com.pq.bean.recordtype;

import java.util.ArrayList;
import java.util.List;

public class FilenameComponents {
	private List<String> patterns;
	private String recType;

	public List<String> getPatterns() {
		if (patterns == null) {
			patterns = new ArrayList<>();
		}
		return patterns;
	}

	public void setPatterns(List<String> patterns) {

		this.patterns = patterns;
	}

	public String getRecType() {
		return recType;
	}

	public void setRecType(String recType) {
		this.recType = recType;
	}

	@Override
	public String toString() {
		return "FilenameComponents [patterns=" + patterns + ", recType=" + recType + "]";
	}

}
