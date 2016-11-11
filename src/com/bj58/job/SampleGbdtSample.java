package com.bj58.job;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.bj58.entity.SampleGbdtFeatureEntity;

public class SampleGbdtSample {
	public static class SampleGbdtSampleMapper extends Mapper<Object, Text, Text, Text> {
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
			SampleGbdtFeatureEntity sgfe = SampleGbdtFeatureEntity.fromJson(line);
			context.write(new Text(sgfe.getQid()), new Text(line));
		}
	}
	public static class SampleGbdtSampleReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			for(Text val: values){
				context.write(val, new Text(""));
			}
		}
	}
}
