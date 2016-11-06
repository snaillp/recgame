package com.bj58.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SampleInfoEntity {
	
	public String toJson()
	{
		return gson.toJson(this);
	}
	
	public static SampleInfoEntity fromJson(String str)
	{
		return gson.fromJson(str, SampleInfoEntity.class);
	}
	
	public String getLable() {
		return lable;
	}

	public void setLable(String lable) {
		this.lable = lable;
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

	public Set<String> getLocal() {
		return local;
	}

	public void setLocal(Set<String> local) {
		this.local = local;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public int getHistCtr() {
		return histCtr;
	}

	public void setHistCtr(int histCtr) {
		this.histCtr = histCtr;
	}

	public long getTimeInteval() {
		return timeInteval;
	}

	public void setTimeInteval(long timeInteval) {
		this.timeInteval = timeInteval;
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

	public double getLocalmatch() {
		return localmatch;
	}

	public void setLocalmatch(double localmatch) {
		this.localmatch = localmatch;
	}

	public double getCatematch() {
		return catematch;
	}

	public void setCatematch(double catematch) {
		this.catematch = catematch;
	}

	public double getSalarymatch() {
		return salarymatch;
	}

	public void setSalarymatch(double salarymatch) {
		this.salarymatch = salarymatch;
	}

	public double getEducationmatch() {
		return educationmatch;
	}

	public void setEducationmatch(double educationmatch) {
		this.educationmatch = educationmatch;
	}

	public double getExperiencematch() {
		return experiencematch;
	}

	public void setExperiencematch(double experiencematch) {
		this.experiencematch = experiencematch;
	}

	public double getEnttypematch() {
		return enttypematch;
	}

	public void setEnttypematch(double enttypematch) {
		this.enttypematch = enttypematch;
	}

	public Set<Integer> getFuliSet() {
		return fuliSet;
	}

	public void setFuliSet(Set<Integer> fuliSet) {
		this.fuliSet = fuliSet;
	}

	public double getFuliMatch() {
		return fuliMatch;
	}

	public void setFuliMatch(double fuliMatch) {
		this.fuliMatch = fuliMatch;
	}

	public int getTrade() {
		return trade;
	}

	public void setTrade(int trade) {
		this.trade = trade;
	}

	public double getTradematch() {
		return tradematch;
	}

	public void setTradematch(double tradematch) {
		this.tradematch = tradematch;
	}

	public static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	public String lable;
	//每行特征
	public String cookie;
	public String infoid;
	public Set<String> local = new HashSet();
	public String cate;
	public int histCtr;
	public long timeInteval; //展示时间-postdate
	public int source;
	public int salary;
	public int education;
	public int experience;
	public int enttype; //公司性质，私营。。。
	public int trade; //行业
	public Set<Integer> fuliSet = new HashSet(16); //福利
	public int fresh; //是否接受应届生
	public int highlights; //职位亮点的个数
	public int additional; //额外要求
	//匹配度
	public double localmatch;
	public double catematch;
	public double salarymatch;
	public double educationmatch;
	public double experiencematch;
	public double enttypematch; //公司性质，私营。。。
	public double tradematch; //行业
	public double fuliMatch;
}
