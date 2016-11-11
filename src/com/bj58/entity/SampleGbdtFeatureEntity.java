package com.bj58.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SampleGbdtFeatureEntity {
	
	public String toJson()
	{
		return gson.toJson(this);
	}
	
	public static SampleGbdtFeatureEntity fromJson(String str)
	{
		return gson.fromJson(str, SampleGbdtFeatureEntity.class);
	}
	
	private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private List<GbdtFeatureUnit> feaList;
	private String label;
	private String qid; //query id
	
	public void addFea(GbdtFeatureUnit gfu){
		if(null == feaList){
			feaList = new ArrayList();
		}
		feaList.add(gfu);
	}
	public List<GbdtFeatureUnit> getFeaList() {
		return feaList;
	}
	public void setFeaList(List<GbdtFeatureUnit> feaList) {
		this.feaList = feaList;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String sep = " ";
		sb.append(label);
		sb.append(sep).append("qid:").append(qid);
		for(GbdtFeatureUnit fea: feaList){
			sb.append(sep).append(fea.toString());
		}
		return sb.toString();
	}
}
