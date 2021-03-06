package com.bj58.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PositionStructEntity {
	
	public PositionStructEntity(){
		
	}
	
	public String toJson()
	{
		return gson.toJson(this);
	}
	
	public static PositionStructEntity fromJson(String str)
	{
		return gson.fromJson(str, PositionStructEntity.class);
	}
	
	public String getInfoid() {
		return infoid;
	}
	public void setInfoid(String infoid) {
		this.infoid = infoid;
	}

	public int getHistCtr() {
		return histCtr;
	}

	public void setHistCtr(int histCtr) {
		this.histCtr = histCtr;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public Set<String> getLocal() {
		return local;
	}

	public void setLocal(Set<String> local) {
		this.local = local;
	}

	public long getPostdate() {
		return postdate;
	}

	public void setPostdate(long postdate) {
		this.postdate = postdate;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public int getEducation() {
		return education;
	}

	public void setEducation(int education) {
		this.education = education;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public int getTrade() {
		return trade;
	}

	public void setTrade(int trade) {
		this.trade = trade;
	}

	public int getEnttype() {
		return enttype;
	}

	public void setEnttype(int enttype) {
		this.enttype = enttype;
	}

	public Set<Integer> getFuli() {
		return fuliSet;
	}

	public void setFuli(Set<Integer> fuliSet) {
		this.fuliSet = fuliSet;
	}

	public int getFresh() {
		return fresh;
	}

	public void setFresh(int fresh) {
		this.fresh = fresh;
	}

	public int getHighlights() {
		return highlights;
	}

	public void setHighlights(int highlights) {
		this.highlights = highlights;
	}

	public int getAdditional() {
		return additional;
	}

	public void setAdditional(int additional) {
		this.additional = additional;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Set<Integer> getFuliSet() {
		return fuliSet;
	}

	public void setFuliSet(Set<Integer> fuliSet) {
		this.fuliSet = fuliSet;
	}

	public int getTitlelen() {
		return titlelen;
	}

	public void setTitlelen(int titlelen) {
		this.titlelen = titlelen;
	}

	public int getUserctr() {
		return userctr;
	}

	public void setUserctr(int userctr) {
		this.userctr = userctr;
	}

	public static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	public String infoid;
	public String userid; //发布用户id
	public int userctr;
	public int titlelen;
	public int histCtr;
	public String cate;
	public Set<String> local = new HashSet(16);
	public long postdate;
	public int source;
	public int salary;
	public int education;
	public int experience;
	public int trade; //行业
	public int enttype; //公司性质，私营。。。
	public Set<Integer> fuliSet = new HashSet(16); //福利
	public int fresh; //是否接受应届生
	public int highlights; //职位亮点的个数
	public int additional; //额外要求
}
