package com.bj58.entity;

public abstract interface BaseFeature {
	public int getFeaIndex(int beginIndex, double value);
	public String getFeaname();
	public int getDimension();
	public double getMin();
	public double getMax();
	public int getBeginIndex();
	public void setBeginIndex(int beginIndex);
	public GbdtFeatureUnit getFea(int beginIndex, double value);
}
