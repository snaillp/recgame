package com.bj58.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumAllFeature implements BaseFeature {
	private String feaname;
	private String featype = "int";
	private int dimension;
	List<Integer> valueList;
	Map<Integer, Integer> indexMap;
	
	public EnumAllFeature(){
		
	}
	public EnumAllFeature(String feaname, List<Integer> vList){
		this.feaname = feaname;
		this.dimension = vList.size();
		this.valueList = vList;
		indexMap = new HashMap();
		for(int i=0; i<this.valueList.size(); ++i){
			indexMap.put(valueList.get(i), i+1);
		}
	}
	public void setValueList(List<Integer> vList){
		this.dimension = vList.size();
		this.valueList = vList;
		if(null == indexMap){
			indexMap = new HashMap();
		}
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
	public String getFeatype() {
		return featype;
	}
	public void setFeatype(String featype) {
		this.featype = featype;
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
	@Override
	public int getFeaIndex(int beginIndex, double value) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getDimension() {
		return this.dimension;
	}
	@Override
	public GbdtFeatureUnit getFea(int beginIndex, int value) {
		int index = getFeaIndex(beginIndex, value);
		return new GbdtFeatureUnit(index, value);
	}
	@Override
	public GbdtFeatureUnit getFea(int beginIndex, double value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args){
		EnumAllFeature freshFea = new EnumAllFeature("fresh", new ArrayList<Integer>(){{add(0); add(1);}});
		System.out.println(freshFea.getFeaIndex(0, 0));
	}
}
