package com.bj58.hadooputil;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//job.setGroupingComparatorClass(SnailGroup.class); //设置自定义分组策略 
public class SnailGroup extends WritableComparator {
	private static final Logger logger = LoggerFactory.getLogger(Text.class);

	public SnailGroup() {
		super(Text.class, true);
	}
	@Override
    public int compare(WritableComparable one,  WritableComparable two) { 
		String oneStr = ((Text)one).toString();
		String[] oneArray = oneStr.split("\001");
		String onekey = oneArray[0];
		String twoStr = ((Text)two).toString();
		String[] twoArray = twoStr.split("\001");
		String twokey = twoArray[0];
		return onekey.compareTo(twokey);
	}
}
