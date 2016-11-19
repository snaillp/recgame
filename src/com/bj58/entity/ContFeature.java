package com.bj58.entity;

public class ContFeature implements BaseFeature  {
	private String feaname;
	private double min;
	private double max;
	private int beginIndex;
	private double interval;
	private int dimension;
	
	public ContFeature(String feaname, double min, double max, int dim)
	{
		this.feaname = feaname;
		this.min = min;
		this.max = max;
		this.dimension = dim;
		this.interval = (max + 1 - min) / this.dimension;
	}
	public ContFeature(String feaname, double min, double max, double interval)
	{
		this.feaname = feaname;
		this.min = min;
		this.max = max;
		this.dimension = (int) ((max - min)/interval)+1;
		this.interval = interval;
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
	public double getInterval() {
		return interval;
	}
	public void setInterval(double interval) {
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
	public int getFeaIndex(int beginIndex, double value){
		if(value - this.max > 0.001){
			value = this.max;
		}
		if(this.min - value > 0.001){
			value = this.min;
		}
		int ind = (int) ((value - min)/interval) + 1;
		return beginIndex + ind;
	}
	
	public static int getFeaIndex(int min, int max, int dim, int beginIndex, double value){
		if(value - max > 0.001){
			value = max;
		}
		if(min - value > 0.001){
			value = min;
		}
		double interval = (max + 1 - min) / dim;
		int ind = (int) ((value - min)/interval) + 1;
		return beginIndex + ind;
	}
	@Override
	public GbdtFeatureUnit getFea(int beginIndex, double value) {
		return new GbdtFeatureUnit(beginIndex+1, value);
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
	
	public static void main(String[] args)
	{	//1:[0,3], 2:[4,6], 3:[7,10], 4:[11-13], 5:[14,16], 6:[17,20]
		//7:[21,23], 8:[24,27], 9:[28,30], 10:[31,33]
		System.out.println("aaaa");
		int min = 0;
		int max = 33;
		int dim = 10;
		ContFeature cf = new ContFeature("testfea", min, max, dim);
		System.out.println(cf.getFeaIndex(0, -1.0));
		System.out.println(ContFeature.getFeaIndex(min, max, dim, 0, 0));
		System.out.println(cf.getDimension());
	}
}
