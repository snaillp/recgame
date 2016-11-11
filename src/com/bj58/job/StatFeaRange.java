package com.bj58.job;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.bj58.entity.SampleInfoEntity;

public class StatFeaRange {
	public static class StatFeaRangeMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			SampleInfoEntity sie = SampleInfoEntity.fromJson(lineArray[1]);
			long timeInterval = sie.getTimeInteval();
			context.write(new Text(String.valueOf(timeInterval)), new Text(""));
		}
	}
	
	public static class StatFeaRangeReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			context.write(key, new Text(""));
		}
	}
}
