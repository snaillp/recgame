package com.bj58.hadooputil;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// job.setPartitionerClass(SanilPartition.class); //设置自定义分区策略 
public class SanilPartition extends Partitioner<Text,Text>{
	private static final Logger logger = LoggerFactory.getLogger(SanilPartition.class); 
	@Override
	public int getPartition(Text key, Text value, int numPartitions) {
		String keyStr = ((Text)key).toString();
		String[] keyArray = keyStr.split("\001");
		String realkey = keyArray[0];
		return  (realkey.hashCode()&Integer.MAX_VALUE)%numPartitions;
	}

}
