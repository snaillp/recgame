package com.bj58.job;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.bj58.entity.ContFeature;
import com.bj58.entity.EnumAllFeature;
import com.bj58.entity.EnumIntervalFeature;
import com.bj58.entity.PositionStructEntity;
import com.bj58.entity.SampleInfoEntity;
import com.bj58.entity.SampleLrFeatureEntity;

public class SampleFeature {
//特征化
	/*
	 * 1.时间戳特征 访问时间-帖子postdate：1万维
	 * 4.历史ctr:10000维
	 * 5.来源（source）: 3维
	 * 6.薪资：0-10,11维
	 * 7.教育：0-8，9维
	 * 8.工作年限：0-7，8维
	 * 9.公司性质：0-10，11维
	 * 9.行业: 0-52, 53维
	 * 10.福利：1-10，10维
	 * 11.是否应届生：0,1，2维
	 * 12.亮点个数：
	 * 13.额外要求：1-4，4维
	 * 14.localmatch:0-4, double, 0.5 interval, 9维
	 * 15.catematch: 0-4, double, 0.5 interval, 9维
	 * 16.salarymatch: 0-1, double, 0.5 interval, 3维
	 * 17.educationmatch: 0-1, double, 0.5 interval, 3维
	 * 18.experiencematch: 0-1, double, 0.5 interval, 3维
	 * 19.enttypematch: 0, 1, double, 1 interval, 2维
	 * 20.fuliMatch: 0-10, double, 1 interval, 11维
	 * 读取用户点击数据，帖子数据，获取原始特征
	 * 1.cont(连续值) min,max dim(多少维)            min,max都是闭区间
	 * 2.enumA e1,e2,... dim(和前面的枚举个数必须相等)  所有枚举值
	 * 3.enumI min,max,interval dim
	 */
	public static class PositionFeatureMapper extends Mapper<Object, Text, Text, Text> {
		private ContFeature timestampFea = new ContFeature("timestamp", 0, 10000, 10000);
		private ContFeature histCtrFea = new ContFeature("histCtr", 0, 10000, 10001);
		private EnumAllFeature sourceFea = new EnumAllFeature("source", new ArrayList<Integer>(){{add(0); add(1);add(2);}});
		private EnumIntervalFeature salaryFea = new EnumIntervalFeature("salary", 0, 10);
		private EnumIntervalFeature eduFea = new EnumIntervalFeature("education", 0, 8);
		private EnumIntervalFeature expFea = new EnumIntervalFeature("experience", 0, 7);
		private EnumIntervalFeature enttypeFea = new EnumIntervalFeature("enttype", 0, 10);
		private EnumIntervalFeature tradeFea = new EnumIntervalFeature("trade", 0,53);
		private EnumIntervalFeature fuliFea = new EnumIntervalFeature("fuli", 0, 10);
		private EnumAllFeature freshFea = new EnumAllFeature("fresh", new ArrayList<Integer>(){{add(0); add(1);}});
		//TODO:统计值范围
		private ContFeature highlightFea = new ContFeature("highlight", 0, 10, 11);
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString();
			String[] lineArray = line.trim().split("\t");
			SampleInfoEntity sie = SampleInfoEntity.fromJson(lineArray[1]);
			SampleLrFeatureEntity sfe = new SampleLrFeatureEntity();
			//时间戳,cont
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
