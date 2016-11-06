package com.bj58.entity;

import java.util.List;
import java.util.Map;

public class EnumAllFeature {
	private String feaname;
	List<Integer> valueList;
	Map<Integer, Integer> indexMap;
	
	public EnumAllFeature(){
		
	}
	public EnumAllFeature(String feaname, List<Integer> vList){
		this.feaname = feaname;
		this.valueList = vList;
		for(int i=1; i<=this.valueList.size(); ++i){
			indexMap.put(valueList.get(i), i);
		}
	}
	public void setValueList(List<Integer> vList){
		this.valueList = vList;
		for(int i=1; i<=this.valueList.size(); ++i){
			indexMap.put(valueList.get(i), i);
		}
	}
	
	public int getFeaIndex(int beginIndex, int value){
		return beginIndex + indexMap.get(value);
	}
}
