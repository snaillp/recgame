package com.bj58.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserIndivEntity {
	
	public String toJson()
	{
		return gson.toJson(this);
	}
	
	public static UserIndivEntity fromJson(String str)
	{
		return gson.fromJson(str, UserIndivEntity.class);
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public List<String> getLocalList() {
		return localList;
	}
	public void setLocalList(List<String> localList) {
		this.localList = localList;
	}
	public List<String> getCateList() {
		return cateList;
	}
	public void setCateList(List<String> cateList) {
		this.cateList = cateList;
	}
	
	
	public static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	String cookie;
	List<String> localList;
	List<String> cateList;
	List<Integer> salaryList;
	List<Integer> educationList;
	List<Integer> experienceList;
	List<Integer> tradeList; //行业
	List<Integer> enttypeList; //公司性质，私营。。。
	List<Integer> fuli; //福利	
}
