package com.bj58.entity;

public class EnumIntervalFeature implements BaseFeature {
	//enumI min,max,interval dim
	private String feaname;
	private String featype = "int";
	private int min;
	private int max;
	private int interval; //
	private int dimension;
	
	public EnumIntervalFeature(String feaname, int min, int max){
		this.feaname = feaname;
		this.min = min;
		this.max = max;
		this.dimension = max - min;
		this.interval = 1;
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
		int ind = value - this.min + 1;
		return beginIndex + ind;
	}
	@Override
	public int getFeaIndex(int beginIndex, double value) {
		// TODO Auto-generated method stub
		return 0;
	}
}
