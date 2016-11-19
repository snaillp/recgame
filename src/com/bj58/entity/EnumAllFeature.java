package com.bj58.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumAllFeature implements BaseFeature {
	private String feaname;
	private int dimension;
	private double min;
	private double max;
	private int beginIndex;
	private int interval = 1;
	List<Double> valueList;
	Map<Double, Integer> indexMap;
	
	public EnumAllFeature(){
		
	}
	public EnumAllFeature(String feaname, List<Double> vList){
		this.feaname = feaname;
		this.dimension = vList.size();
		this.valueList = vList;
		indexMap = new HashMap();
		for(int i=0; i<this.valueList.size(); ++i){
			double value = valueList.get(i);
			if(min > value){
				min = value;
			}
			if(max < value){
				max = value;
			}
			indexMap.put(value, i+1);
		}
	}
	public void setValueList(List<Double> vList){
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
	public Map<Double, Integer> getIndexMap() {
		return indexMap;
	}
	public void setIndexMap(Map<Double, Integer> indexMap) {
		this.indexMap = indexMap;
	}
	public List<Double> getValueList() {
		return valueList;
	}
	public int getFeaIndex(int beginIndex, double value){
		return beginIndex + indexMap.get(value);
	}
	
	public int getDimension() {
		return this.dimension;
	}
	public int getBeginIndex() {
		return beginIndex;
	}
	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public void setMax(double max) {
		this.max = max;
	}
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(feaname).append(":{").append("value:[").append(this.min).append(",");
		sb.append(this.max).append("]").append(", index:[").append(this.beginIndex);
		sb.append(",").append(this.beginIndex+this.dimension-1).append("]").append(", dimension:").append(this.dimension);
		sb.append(", interval:").append(this.interval);
		return sb.toString();
	}
	@Override
	public GbdtFeatureUnit getFea(int beginIndex, double value) {
		int index = getFeaIndex(beginIndex, value);
		return new GbdtFeatureUnit(index, 1.0);
	}

	public static void main(String[] args){
		EnumAllFeature freshFea = new EnumAllFeature("fresh", new ArrayList<Double>(){{add(0.0); add(1.0);}});
		System.out.println(freshFea.getFeaIndex(0, 0));
	}
	@Override
	public double getMin() {
		return this.min;
	}
	@Override
	public double getMax() {
		return this.max;
	}
}
