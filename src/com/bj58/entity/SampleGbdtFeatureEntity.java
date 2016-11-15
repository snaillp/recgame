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
	private String cookie;
	private String infoid;
	private boolean needNotes = true;
	
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

	public boolean isNeedNotes() {
		return needNotes;
	}

	public void setNeedNotes(boolean needNotes) {
		this.needNotes = needNotes;
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
		if(this.needNotes){
			sb.append(sep).append("#").append(cookie).append("\t").append(infoid);
		}
		return sb.toString();
	}
}
