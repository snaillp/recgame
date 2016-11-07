package com.bj58.entity;

public abstract interface BaseFeature {
	public int getFeaIndex(int beginIndex, double value);
	public int getFeaIndex(int beginIndex, int value);
	public String getFeaname();
	public String getFeatype();
	public int getDimension();
}
