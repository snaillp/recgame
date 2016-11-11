package com.bj58.job;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class SampleLrSample {
	public static class SampleLrSampleMapper extends Mapper<Object, Text, Text, Text> {
		private int sampleLevel = 10;
		private Random random = new Random();
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			sampleLevel = context.getConfiguration().getInt("sampleLevel", 10);
		}
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString().trim();
			if(line.startsWith("0")){
				int randno = random.nextInt(sampleLevel);
				if(randno != 0){
					return;
				}
			}
			context.write(new Text(line), new Text(""));
		}
	}
}
