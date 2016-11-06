package com.bj58.entity;

import java.util.List;
import java.util.Map;

public class EnumAllFeature {
	private String feaname;
	private int dimention;
	List<Integer> valueList;
	Map<Integer, Integer> indexMap;
	
	public EnumAllFeature(){
		
	}
	public EnumAllFeature(String feaname, List<Integer> vList){
		this.feaname = feaname;
		this.dimention = vList.size();
		this.valueList = vList;
		for(int i=1; i<=this.valueList.size(); ++i){
			indexMap.put(valueList.get(i), i);
		}
	}
	public void setValueList(List<Integer> vList){
		this.dimention = vList.size();
		this.valueList = vList;
		for(int i=1; i<=this.valueList.size(); ++i){
			indexMap.put(valueList.get(i), i);
		}
	}
	
	public String getFeaname() {
		return feaname;
	}
	public void setFeaname(String feaname) {
		this.feaname = feaname;
	}
	public int getDimention() {
		return dimention;
	}
	public void setDimention(int dimention) {
		this.dimention = dimention;
	}
	public Map<Integer, Integer> getIndexMap() {
		return indexMap;
	}
	public void setIndexMap(Map<Integer, Integer> indexMap) {
		this.indexMap = indexMap;
	}
	public List<Integer> getValueList() {
		return valueList;
	}
	public int getFeaIndex(int beginIndex, int value){
		return beginIndex + indexMap.get(value);
	}
}
