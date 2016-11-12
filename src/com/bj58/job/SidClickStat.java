package com.bj58.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.bj58.entity.ClickInfoEntity;

public class SidClickStat {
	public static class SidClickStatMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString().trim();
			String[] lineArray = line.split("\001");
			String cookie = lineArray[0];
			String sid = lineArray[3];
			String infoid = lineArray[19];
			String label = lineArray[23];
			context.write(new Text(sid), new Text(sid+"\001"+label+"\001"+cookie+"\001"+infoid));
		}
	}
	public static class SidClickStatReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			List<String> entityList = new ArrayList();
			int count0 = 0;
			int count1 = 0;
			int total = 0;
			for(Text val: values){
				String vl = val.toString();
				String[] vlArray = vl.split("\001");
				String sid = vlArray[0];
				String label = vlArray[1];
				if(label.equals("0")){
					count0++;
				}else{
					count1++;
				}
				total++;
//				entityList.add(vl);
			}
//			int entityNum = entityList.size();
//			if(count0 == entityNum || count1 == entityNum){
				context.write(new Text(key), new Text(count0+"\001"+count1+"\001"+total));
//			}
//			for(String val: entityList){
//				context.write(new Text(val), new Text(entityNum+"\001"+count0+"\001"+count1));
//			}
		}
	}
}
