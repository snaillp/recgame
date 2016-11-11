package com.bj58.entity;

import java.util.ArrayList;
import java.util.List;

public class SampleLrFeatureEntity {
	private List<Integer> feaList = new ArrayList();
	private String label;
	private String cookie;
	private String infoid;
	
	public void addFea(int fea){
		feaList.add(fea);
	}
	
	public List<Integer> getFeaList() {
		return feaList;
	}

	public void setFeaList(List<Integer> feaList) {
		this.feaList = feaList;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getInfoid() {
		return infoid;
	}

	public void setInfoid(String infoid) {
		this.infoid = infoid;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String sep = " ";
		sb.append(label);
		for(int fea: feaList){
			sb.append(sep).append(fea).append(":1");
		}
		sb.append(sep).append("#").append(cookie).append("\t").append(infoid);
		return sb.toString();
	}
}
