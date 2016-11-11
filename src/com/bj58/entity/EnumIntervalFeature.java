package com.bj58.entity;

import java.util.Map;

public class EnumIntervalFeature implements BaseFeature {
	//enumI min,max,interval dim
	private String feaname;
	private String featype = "int";
	private int min;
	private int max;
	private int interval; //
	private int dimension;
//	Map<Integer, Integer> indexMap;
	
	public EnumIntervalFeature(String feaname, int min, int max){
		this.feaname = feaname;
		this.min = min;
		this.max = max;
		this.dimension = max - min + 1;
		this.interval = 1;
//		for(int i=this.min; i<=this.max; ++i){
//			indexMap.put(i, i-this.min+1);
//		}
	}
	public EnumIntervalFeature(){
		
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
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getDimension() {
		return dimension;
	}
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
	public int getFeaIndex(int beginIndex, int value){
		if(value>this.max || value<this.min){
			value = min;
		}
		int ind = value - this.min + 1;
		return beginIndex + ind;
	}
	@Override
	public int getFeaIndex(int beginIndex, double value) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public GbdtFeatureUnit getFea(int beginIndex, double value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public GbdtFeatureUnit getFea(int beginIndex, int value) {
		int index = getFeaIndex(beginIndex, value);
		return new GbdtFeatureUnit(index, value);
	}
	
	public static void main(String[] args)
	{
		EnumIntervalFeature sourceFea = new EnumIntervalFeature("source", 0, 7);
		System.out.println(sourceFea.getDimension());
		System.out.println(sourceFea.getFeaIndex(100, 3));
	}
}
