package com.bj58.job;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.bj58.entity.PositionStructEntity;

public class PositionFeature {
//特征化
	/*
	 * 1.时间戳特征 访问时间-帖子postdate：1万维
	 * 2.local符合度, 一二三级，越细得分越高：0-4,5维
	 * 3.cate符合度：0-3,4维
	 * 4.历史ctr:1000维
	 * 5.来源（source）
	 * 6.薪资：0-10,11维
	 * 7.教育：0-8，9维
	 * 8.工作年限：0-7，8维
	 * 9.公司性质：0-10，11维
	 * 10.福利：1-10，10维
	 * 11.是否应届生：0,1，2维
	 * 12.亮点个数：
	 * 13.额外要求：1-4，4维
	 * 读取用户点击数据，帖子数据，获取原始特征
	 */
	public static class PositionFeatureMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString();
			String[] lineArray = line.trim().split("\t");
			String flag = "";
			if(inputfile.contains("/structdata/")){
				//infoid
				flag = "A";
			}else if(inputfile.contains("")){
				//ctr
				flag = "B";
			}else if(inputfile.contains("")){
				//click
				flag = "C";
			}
			if(!flag.isEmpty()){
				context.write(new Text(lineArray[0]), new Text(flag+"\001"+lineArray[1]));
			}
		}
	}
	
	public static class PositionFeatureReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			for(Text val: values){
				String vl = val.toString().trim();
				String vlInfo = vl.substring(2);
				int ctr = 0;
				PositionStructEntity pse = null;
				if(vl.startsWith("A")){
					pse = PositionStructEntity.fromJson(vlInfo);
				}else if(vl.startsWith("B")){
					ctr = Integer.parseInt(vlInfo);
				}else if(vl.startsWith("C")){
					
				}
			}
		}
	}
}
