package com.bj58.job;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class MergeInput {
	public static class MergeInputMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			context.write(value, new Text(""));
		}
	}
}
