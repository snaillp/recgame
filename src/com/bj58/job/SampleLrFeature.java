package com.bj58.job;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.bj58.entity.BaseFeature;
import com.bj58.entity.ContFeature;
import com.bj58.entity.EnumAllFeature;
import com.bj58.entity.EnumIntervalFeature;
import com.bj58.entity.PositionStructEntity;
import com.bj58.entity.SampleInfoEntity;
import com.bj58.entity.SampleLrFeatureEntity;

public class SampleLrFeature {
//特征化
	/*
	 * 1.时间戳特征 访问时间-帖子postdate：1万维
	 * 2.历史ctr:10000维
	 * 3.来源（source）: 3维
	 * 4.薪资：0-10,11维
	 * 5.教育：0-8，9维
	 * 6.工作年限：0-7，8维
	 * 7.公司性质：0-10，11维
	 * 8.行业: 0-52, 53维
	 * 9.福利：1-10，10维
	 * 10.是否应届生：0,1，2维
	 * 11.亮点个数：
	 * 12.额外要求：0-4，4维
	 * 13.localmatch:0-4, double, 0.5 interval, 9维
	 * 14.catematch: 0-4, double, 0.5 interval, 9维
	 * 15.salarymatch: 0-1, double, 0.5 interval, 3维
	 * 16.educationmatch: 0-1, double, 0.5 interval, 3维
	 * 17.experiencematch: 0-1, double, 0.5 interval, 3维
	 * 18.enttypematch: 0, 1, double, 1 interval, 2维
	 * 19.tradematch:0-1, double, 1 interval, 2维
	 * 20.fuliMatch: 0-10, double, 1 interval, 11维
	 * 读取用户点击数据，帖子数据，获取原始特征
	 * 1.cont(连续值) min,max dim(多少维)            min,max都是闭区间
	 * 2.enumA e1,e2,... dim(和前面的枚举个数必须相等)  所有枚举值
	 * 3.enumI min,max,interval dim
	 */
	public static class SampleLrFeatureMapper extends Mapper<Object, Text, Text, Text> {
		//min:0, max:253617349, avg:11199388
		private ContFeature timestampFea = new ContFeature("timeInteval", 0, 260000000, 10000);
		private ContFeature histCtrFea = new ContFeature("histCtr", 0, 10000, 10001);
		//TODO:统计值范围
		private EnumIntervalFeature sourceFea = new EnumIntervalFeature("source", 0, 15);
		private EnumIntervalFeature salaryFea = new EnumIntervalFeature("salary", 0, 10);
		private EnumIntervalFeature eduFea = new EnumIntervalFeature("education", 0, 8);
		private EnumIntervalFeature expFea = new EnumIntervalFeature("experience", 0, 7);
		private EnumIntervalFeature enttypeFea = new EnumIntervalFeature("enttype", 0, 10);
		private EnumIntervalFeature tradeFea = new EnumIntervalFeature("trade", 0,53);
		//福利是多值，特殊处理
		private EnumIntervalFeature fuliFea = new EnumIntervalFeature("fuliSet", 0, 10);
		private EnumAllFeature freshFea = new EnumAllFeature("fresh", new ArrayList<Integer>(){{add(0); add(1);}});
		//TODO:统计值范围
		private ContFeature highlightFea = new ContFeature("highlights", 0, 15, 16);
		private EnumIntervalFeature additionFea = new EnumIntervalFeature("additional", 0, 4);
		private ContFeature localmatchFea = new ContFeature("localmatch", 0, 4, 0.5);
		private ContFeature catematchFea = new ContFeature("catematch", 0, 4, 0.5);
		private ContFeature salarymatchFea = new ContFeature("salarymatch", 0, 1, 0.5);
		private ContFeature edumatchFea = new ContFeature("educationmatch", 0, 1, 0.5);
		private ContFeature expmatchFea = new ContFeature("experiencematch", 0, 1, 0.5);
		private EnumAllFeature entmatchFea = new EnumAllFeature("enttypematch", new ArrayList<Integer>(){{add(0); add(1);}});
		private EnumAllFeature tradematchFea = new EnumAllFeature("tradematch", new ArrayList<Integer>(){{add(0); add(1);}});
		private ContFeature fulimatchFea = new ContFeature("fuliMatch", 0, 10, 1);
		List<BaseFeature> feaList = new ArrayList(){{
			add(timestampFea);
			add(histCtrFea);
			add(sourceFea);
			add(salaryFea);
			add(eduFea);
			add(expFea);
			add(enttypeFea);
			add(tradeFea);
			add(freshFea);
			add(highlightFea);
			add(additionFea);
			add(localmatchFea);
			add(catematchFea);
			add(salarymatchFea);
			add(edumatchFea);
			add(expmatchFea);
			add(entmatchFea);
			add(tradematchFea);
			add(fulimatchFea);
			}};
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
//			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString();
			String[] lineArray = line.trim().split("\t");
			SampleInfoEntity sie = SampleInfoEntity.fromJson(lineArray[1]);
			SampleLrFeatureEntity sfe = new SampleLrFeatureEntity();
			sfe.setLabel(sie.getLable());
			//公共变量
			int beginIndex = 0;
			int feaIndex = 0;
			for(BaseFeature fea: feaList){
				String fieldname = fea.getFeaname();
				
				try {
					Field field = sie.getClass().getDeclaredField(fieldname);
					field.setAccessible(true);
					field.get(sie);
					if(fea.getFeatype().equals("double")){
						feaIndex = fea.getFeaIndex(beginIndex, field.getDouble(sie));
					}else if(fea.getFeatype().equals("int")){
						feaIndex = fea.getFeaIndex(beginIndex, field.getInt(sie));
					}
					sfe.addFea(feaIndex);
					beginIndex += fea.getDimension();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			//特殊处理福利
			Set<Integer> fuliSet = sie.getFuliSet();
			for(int fl: fuliSet){
				feaIndex = fuliFea.getFeaIndex(beginIndex, fl);
				sfe.addFea(feaIndex);
			}
			context.write(new Text(sfe.toString()), new Text(""));
//			//时间戳,cont
//			long timestamp = sie.getTimeInteval();
//			feaIndex = timestampFea.getFeaIndex(beginIndex, timestamp);
//			sfe.addFea(feaIndex);
//			beginIndex += timestampFea.getDimension();
//			//histCtr
//			int histCtr = sie.getHistCtr();
//			feaIndex = histCtrFea.getFeaIndex(beginIndex, histCtr);
//			sfe.addFea(feaIndex);
//			beginIndex += histCtrFea.getDimension();
//			//source
//			int source = sie.getSource();
//			feaIndex = sourceFea.getFeaIndex(beginIndex, source);
//			sfe.addFea(feaIndex);
//			beginIndex += sourceFea.getDimention();
//			//salary
//			int salary = sie.getSalary();
//			feaIndex = salaryFea.getFeaIndex(beginIndex, salary);
//			sfe.addFea(feaIndex);
//			beginIndex += salaryFea.getDimension();
//			//edu
//			int edu = sie.getEducation();
//			feaIndex = eduFea.getFeaIndex(beginIndex, edu);
//			sfe.addFea(feaIndex);
//			beginIndex += eduFea.getDimension();
		}
	}
	
	public static class SampleLrFeatureReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			
		}
	}
}
