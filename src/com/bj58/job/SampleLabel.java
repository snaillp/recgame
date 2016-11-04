package com.bj58.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.bj58.entity.ClickInfoEntity;
import com.bj58.entity.PositionStructEntity;
import com.bj58.entity.SampleInfoEntity;

public class SampleLabel {
	/*
	 * 输入：1.info的imc信息 2.info的点击状态
	 */
	public static class SampleLabelMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			if(inputfile.contains("tracklog/")){
				//imc
//				PositionStructEntity pse = PositionStructEntity.fromJson(lineArray[1]);
				context.write(new Text(lineArray[0]), new Text("A\001"+lineArray[1]));
			}else if(inputfile.contains("")){
//				ClickInfoEntity cie = ClickInfoEntity.fromJson(lineArray[1]);
				context.write(new Text(lineArray[0]), new Text("B\001"+lineArray[1]));
			}
		}
	}
	public static class SampleLabelReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			PositionStructEntity pse = null;
			for(Text val: values){
				String vl = val.toString();
				if(vl.startsWith("A")){
					pse = PositionStructEntity.fromJson(vl.substring(2));
					break;
				}
			}
			if(pse != null){
				for(Text val: values){
					String vl = val.toString();
					if(vl.startsWith("B")){
						vl = vl.substring(2);
						ClickInfoEntity cie = ClickInfoEntity.fromJson(vl);
						SampleInfoEntity sie = new SampleInfoEntity();
						sie.timeInteval = cie.getVisittime() - pse.getPostdate();
						sie.lable = cie.getLabel();
						sie.cookie = cie.getCookie();
						sie.infoid = cie.getInfoid();
						fromePosition2Sample(pse, sie);
					}
				}
			}
		}
		private void fromePosition2Sample(PositionStructEntity pse, SampleInfoEntity sie)
		{
			sie.source = pse.source;
			sie.salary = pse.salary;
			sie.education = pse.education;
			sie.experience = pse.experience;
			sie.enttype = pse.enttype; //公司性质，私营。。。
			sie.fuli = pse.fuli; //福利
			sie.fresh = pse.fresh; //是否接受应届生
			sie.highlights = pse.highlights; //职位亮点的个数
			sie.additional = pse.additional; //额外要求
		}
	}
}
