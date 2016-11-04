package com.bj58.entity;

import java.util.ArrayList;
import java.util.List;

public class SampleInfoEntity {
	public String lable;
	//每行特征
	public String cookie;
	public String infoid;
	public List<String> local;
	public String cate;
	public int histCtr;
	public long timeInteval; //展示时间-postdate
	public int source;
	public int salary;
	public int education;
	public int experience;
	public int enttype; //公司性质，私营。。。
	public List<Integer> fuli = new ArrayList(16); //福利
	public int fresh; //是否接受应届生
	public int highlights; //职位亮点的个数
	public int additional; //额外要求
	//匹配度
	public String localmatch;
	public String catematch;
	public int salarymatch;
	public int educationmatch;
	public int experiencematch;
	public int enttypematch; //公司性质，私营。。。
	
}
