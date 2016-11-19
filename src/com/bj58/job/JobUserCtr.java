package com.bj58.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.bj58.entity.PositionStructEntity;

public class JobUserCtr {
	//得到user和info的对应关系
	public static class UserInfoidMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			PositionStructEntity pse = PositionStructEntity.fromJson(lineArray[1]);
			String userid = pse.getUserid();
			if(null != userid){
				context.write(new Text(lineArray[0]), new Text(userid));
			}
		}
	}
	public static class UserInfoidReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			Set<String> userSet = new HashSet();
			for(Text val: values){
				String userid = val.toString();
				if(!userSet.contains(userid)){
					context.write(key, new Text(userid));
					userSet.add(userid);
				}
			}
		}
	}
	//将infoID的click-show映射到userid
	public static class JobUserMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			if(inputfile.contains("/Ctr/")){
				//format: infoid click show ctr
				String infoid = lineArray[0];
				String clicknum = lineArray[1];
				String shownum = lineArray[2];
				context.write(new Text(infoid+"\001A"), new Text("A\001"+clicknum+"\001"+shownum));
			}else if(inputfile.contains("/userinfoid/")){
				//format: infoid\tuserid
				context.write(new Text(lineArray[0]+"\001B"), new Text("B\001"+lineArray[1]));
			}
		}
	}
	public static class JobUserReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			String clickshow = null;
			for(Text val: values){
				String vl = val.toString();
				if(vl.startsWith("A")){
					clickshow = vl.substring(2);
				}else if(vl.startsWith("B")){
					if(null != clickshow){
						String userid = vl.substring(2);
						context.write(new Text(userid), new Text(clickshow));
					}
				}
			}
		}
	}
	//计算user ctr
	public static class JobUserCtrMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			String userid = lineArray[0];
			String clickshow = lineArray[1];
			context.write(new Text(userid), new Text(clickshow));
		}
	}
	public static class JobUserCtrReducer extends Reducer<Text, Text, Text, Text> {
		
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			int clicknum = 0;
			int shownum = 0;
			for(Text val: values){
				String vl = val.toString();
				String[] vlArray = vl.split("\001");
				if(vlArray.length != 2){
					continue;
				}
				clicknum += Integer.parseInt(vlArray[0]);
				shownum += Integer.parseInt(vlArray[1]);
			}
			if(shownum == 0){
				return;
			}
			double ctr = clicknum/(double)shownum;
			context.write(key, new Text(String.valueOf(ctr)));
		}
	}
	//加入样本
	//计算user ctr
	public static class PositionUserCtrMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit) context.getInputSplit()).getPath().toString();
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			if (inputfile.contains("/jobuserctr/")) {
				String userid = lineArray[0];
				String ctr = lineArray[1];
				context.write(new Text(userid + "\001A"), new Text("A\001" + ctr));
			} else if (inputfile.contains("/structdata/")) {
				// 帖子数据， key: infoID，value: info
				PositionStructEntity pse = PositionStructEntity.fromJson(lineArray[1]);
				context.write(new Text(pse.getUserid() + "\001B"), new Text("B\001" + lineArray[1]));
			}
		}
	}

	public static class PositionUserCtrReducer extends Reducer<Text, Text, Text, Text> {
		private int ctrLevel = 10000;
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			ctrLevel = context.getConfiguration().getInt("ctrLevel", 10000);
		}
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			String ctr = null;
			for (Text val : values) {
				String vl = val.toString();
				if (vl.startsWith("B")) {
					PositionStructEntity pse = PositionStructEntity.fromJson(vl.substring(2));
					int defaultCtr = (int)(0.011930423*ctrLevel);
					if(null != ctr){
						pse.setUserctr((int)(Double.parseDouble(ctr)*ctrLevel));
					}else{
						pse.setUserctr(defaultCtr);
					}
					context.write(new Text(pse.getInfoid()), new Text(pse.toJson()));
				} else if (vl.startsWith("A")) {
					ctr = vl.substring(2);
				}
			}
		}
	}
}
