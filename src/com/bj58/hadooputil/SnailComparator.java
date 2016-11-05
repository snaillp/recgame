package com.bj58.hadooputil;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//job.setSortComparatorClass(SnailComparator.class); //设置自定义二次排序策略 
public class SnailComparator extends WritableComparator {
	private static final Logger logger = LoggerFactory.getLogger(Text.class);

	public SnailComparator() {
		super(Text.class, true);
	}
	@Override
    public int compare(WritableComparable one,  WritableComparable two) { 
		String oneStr = ((Text)one).toString();
		String twoStr = ((Text)two).toString();
		return oneStr.compareTo(twoStr);
	}
}
