package com.bj58.entity;

public class GbdtFeatureUnit {
	private int index;
	private double value;
	
	public GbdtFeatureUnit(){
		
	}
	public GbdtFeatureUnit(int index, double value) {
		super();
		this.index = index;
		this.value = value;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	@Override
	public String toString(){
		return index+":"+value;
	}
}
