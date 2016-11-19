package com.bj58.job;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.bj58.entity.ClickInfoEntity;
import com.bj58.entity.PositionStructEntity;
import com.bj58.entity.SampleInfoEntity;

public class TestSampleWithPositionLabel {
	/*
	 * 输入：1.info的imc信息 2.info的点击状态
	 */
	public static class TestSampleWithPositionLabelMapper extends Mapper<Object, Text, Text, Text> {
		private long defaultvisittime = 0;
		@Override
		protected void setup(Context context) throws IOException, InterruptedException 
		{
			DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
			try {
				Date begin = df.parse("2016-09-26");
				defaultvisittime = begin.getTime()/(5*60*1000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString().trim();
			
			if(inputfile.contains("/positionuserctr/")){
				String[] lineArray = line.split("\t");
				//imc, key:infoid
//				PositionStructEntity pse = PositionStructEntity.fromJson(lineArray[1]);
				context.write(new Text(lineArray[0]+"\001A"), new Text("A\001"+lineArray[1]));
			}else if(inputfile.contains("/testdata/")){
				String[] lineArray = line.split("\001");
				//click, key:infoid
				ClickInfoEntity cie = new ClickInfoEntity();
				cie.setCookie(lineArray[0]);
				cie.setInfoid(lineArray[1]);
				cie.setVisittime(defaultvisittime);
				cie.setLabel("0");
				cie.setSid("0");
				context.write(new Text(cie.getInfoid()+"\001B"), new Text("B\001"+cie.toJson()));
			}
		}
	}
	public static class TestSampleWithPositionLabelReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			PositionStructEntity pse = null;
			for(Text val: values){
				String vl = val.toString();
				if(vl.startsWith("A")){
					if(pse == null){
						pse = PositionStructEntity.fromJson(vl.substring(2));
					}else{
						PositionStructEntity pseN = PositionStructEntity.fromJson(vl.substring(2));
						if(pseN.getPostdate() > pse.getPostdate()){
							pse = pseN;
						}
					}
				}else if(vl.startsWith("B")){
					ClickInfoEntity cie = ClickInfoEntity.fromJson(vl.substring(2));
					if(pse != null){
						SampleInfoEntity sie = new SampleInfoEntity();
						long timeInterval = cie.getVisittime() - pse.getPostdate();
						if(timeInterval < 0){
							sie.timeInteval = 0;
						}else{
							sie.timeInteval = timeInterval;
						}
						sie.sid = cie.getSid();
						sie.lable = cie.getLabel();
						sie.cookie = cie.getCookie();
						sie.infoid = cie.getInfoid();
						fromePosition2Sample(pse, sie);
						context.write(new Text(sie.cookie), new Text(sie.toJson()));
					}
				}
			}
		}
		private void fromePosition2Sample(PositionStructEntity pse, SampleInfoEntity sie)
		{
			sie.local = pse.local;
			sie.cate = pse.cate;
			sie.histCtr = pse.histCtr;
			sie.postdate = pse.postdate;
			sie.source = pse.source;
			sie.salary = pse.salary;
			sie.education = pse.education;
			sie.experience = pse.experience;
			sie.enttype = pse.enttype; //公司性质，私营。。。
			sie.trade = pse.trade;
			sie.fuliSet = pse.fuliSet; //福利
			sie.fresh = pse.fresh; //是否接受应届生
			sie.highlights = pse.highlights; //职位亮点的个数
			sie.additional = pse.additional; //额外要求
		}
	}
}
