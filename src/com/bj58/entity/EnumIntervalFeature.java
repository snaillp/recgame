package com.bj58.entity;

import java.util.Map;

public class EnumIntervalFeature implements BaseFeature {
	//enumI min,max,interval dim
	private String feaname;
	private double min;
	private double max;
	private int beginIndex;
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
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
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
	public int getBeginIndex() {
		return beginIndex;
	}
	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
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
	public int getFeaIndex(int beginIndex, double value){
		if(value>this.max || value<this.min){
			value = min;
		}
		int ind = (int) (value - this.min + 1);
		return beginIndex + ind;
	}

	@Override
	public GbdtFeatureUnit getFea(int beginIndex, double value) {
		int index = getFeaIndex(beginIndex, value);
		return new GbdtFeatureUnit(index, 1.0);
	}
	
	public static void main(String[] args)
	{
		EnumIntervalFeature sourceFea = new EnumIntervalFeature("source", 0, 7);
		System.out.println(sourceFea.getDimension());
		System.out.println(sourceFea.getFeaIndex(100, 3));
	}
}
