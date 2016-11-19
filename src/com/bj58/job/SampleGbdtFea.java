package com.bj58.job;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.bj58.entity.BaseFeature;
import com.bj58.entity.ContFeature;
import com.bj58.entity.EnumAllFeature;
import com.bj58.entity.EnumIntervalFeature;
import com.bj58.entity.GbdtFeatureUnit;
import com.bj58.entity.SampleGbdtFeatureEntity;
import com.bj58.entity.SampleInfoEntity;

public class SampleGbdtFea {
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
	public static class SampleGbdtFeaMapper extends Mapper<Object, Text, Text, Text> {
		private boolean needNotes = true; //是否需要在样本末尾加入cookie，infoID等注释 
		private String feafilepath = null;
		// TODO:统计值范围
		// private ContFeature timestampFea = new ContFeature("timeInteval", 0, 1, 1);
		private ContFeature postdateFea = new ContFeature("postdate", 1000000, 4999999, 1);
		private ContFeature histCtrFea = new ContFeature("histCtr", 0, 9999, 1);
		private ContFeature uerCtrFea = new ContFeature("userCtr", 0, 9999, 1);
		// TODO:统计值范围
//		private EnumIntervalFeature sourceFea = new EnumIntervalFeature("source", 0, 15);
//		private EnumIntervalFeature salaryFea = new EnumIntervalFeature("salary", 0, 10);
//		private EnumIntervalFeature eduFea = new EnumIntervalFeature("education", 0, 8);
//		private EnumIntervalFeature expFea = new EnumIntervalFeature("experience", 0, 7);
		private ContFeature salaryFea = new ContFeature("salary", 0, 1, 1);
		private ContFeature eduFea = new ContFeature("education", 0, 1, 1);
		private ContFeature expFea = new ContFeature("experience", 0, 1, 1);
		private EnumIntervalFeature enttypeFea = new EnumIntervalFeature("enttype", 0, 10);
//		private EnumIntervalFeature tradeFea = new EnumIntervalFeature("trade", 0,53);
		// 福利是多值，特殊处理
		private EnumIntervalFeature fuliFea = new EnumIntervalFeature("fuliSet", 0, 10);
		private EnumAllFeature freshFea = new EnumAllFeature("fresh", new ArrayList<Double>(){{ add(0.0);add(1.0);}});
		// TODO:统计值范围
		private ContFeature highlightFea = new ContFeature("highlights", 0, 1, 1);
		private EnumIntervalFeature additionFea = new EnumIntervalFeature("additional", 0, 4);
//		private ContFeature localmatchFea = new ContFeature("localmatch", 0, 1, 1);
//		private ContFeature catematchFea = new ContFeature("catematch", 0, 1, 1);
//		private ContFeature salarymatchFea = new ContFeature("salarymatch", 0, 1, 1);
//		private ContFeature edumatchFea = new ContFeature("educationmatch", 0, 1, 1);
//		private ContFeature expmatchFea = new ContFeature("experiencematch", 0, 1, 1);
//		private ContFeature entmatchFea = new ContFeature("enttypematch", 0, 1, 1);
//		private ContFeature tradematchFea = new ContFeature("tradematch", 0, 1, 1);
//		private ContFeature fulimatchFea = new ContFeature("fuliMatch", 0, 1, 1);
		List<BaseFeature> feaList = new ArrayList() {
			{
				// add(timestampFea);
				add(postdateFea);
				add(histCtrFea);
				add(uerCtrFea);
				// add(sourceFea);
				add(salaryFea);
				add(eduFea);
				add(expFea);
				add(enttypeFea);
				// add(tradeFea);
				add(freshFea);
				add(highlightFea);
				add(additionFea);
				// add(localmatchFea);
				// add(catematchFea);
				// add(salarymatchFea);
				// add(edumatchFea);
				// add(expmatchFea);
				// add(entmatchFea);
				// add(tradematchFea);
				// add(fulimatchFea);
			}
		};
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			needNotes = context.getConfiguration().getBoolean("needNotes", true);
			feafilepath = context.getConfiguration().get("feaDescPath", null); 
			if(null == feafilepath && !feafilepath.isEmpty()){
				//特征说明文件
				StringBuilder sb = new StringBuilder();
				int beginIndex = 1;
				for(BaseFeature fea: feaList){
					fea.setBeginIndex(beginIndex);
					sb.append(fea.toString()).append("\n");
					beginIndex += fea.getDimension();
				}
				fuliFea.setBeginIndex(beginIndex);
				sb.append(fuliFea.toString());
				
				if(sb.length() != 0){
					FileSystem fs = FileSystem.get(context.getConfiguration());
					Path path = new Path(feafilepath);
					FSDataOutputStream out = fs.create(path, true);
					out.writeBytes(sb.toString());
					out.close();
				}
			}
		}
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] lineArray = line.trim().split("\t");
			SampleInfoEntity sie = SampleInfoEntity.fromJson(lineArray[1]);
			SampleGbdtFeatureEntity sgfe = new SampleGbdtFeatureEntity();
			sgfe.setLabel(sie.getLable());
			sgfe.setQid(sie.getSid());
			sgfe.setCookie(sie.getCookie());
			sgfe.setInfoid(sie.getInfoid());
			sgfe.setNeedNotes(needNotes);
			//公共变量
			int beginIndex = 0;
			GbdtFeatureUnit feaUnit = null;
			for(BaseFeature fea: feaList){
				String fieldname = fea.getFeaname();
				try {
					Field field = sie.getClass().getDeclaredField(fieldname);
					field.setAccessible(true);
					field.get(sie);
//					if(fea.getFeatype().equals("double")){
					feaUnit = fea.getFea(beginIndex, field.getDouble(sie));
//					}else if(fea.getFeatype().equals("int")){
//						feaUnit = fea.getFea(beginIndex, field.getInt(sie));
//					}
					if(null != feaUnit){
						sgfe.addFea(feaUnit);
					}
					beginIndex += fea.getDimension();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			//特殊处理福利
			Set<Integer> fuliSet = sie.getFuliSet();
			for(int fl: fuliSet){
				feaUnit = fuliFea.getFea(beginIndex, fl);
				sgfe.addFea(feaUnit);
			}
			context.write(new Text(sgfe.getQid()), new Text(sgfe.toString()));
		}
	}
	
	public static class SampleGbdtFeaReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			for(Text val: values){
				context.write(val, new Text(""));
			}
		}
	}
}
