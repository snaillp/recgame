package com.bj58.entity;

import java.util.ArrayList;
import java.util.List;

public class SampleLrFeatureEntity {
	private List<Integer> feaList = new ArrayList();
	private String label;
	
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

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String sep = " ";
		sb.append(label);
		for(int fea: feaList){
			sb.append(sep).append(fea).append(":1");
		}
		return sb.toString();
	}
}
