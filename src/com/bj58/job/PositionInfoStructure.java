package com.bj58.job;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.bj58.entity.PositionStructEntity;

public class PositionInfoStructure {
	public static class PositionInfoStructureMapper extends Mapper<Object, Text, Text, Text> {

		public PositionStructEntity parseEntity(String[] lineArray)
		{
			PositionStructEntity pse = new PositionStructEntity();
			pse.infoid = lineArray[0];
			if(lineArray[4].matches("\\d+")){
				pse.cate = lineArray[4];
			}else if(lineArray[3].matches("\\d+")){
				pse.cate = lineArray[3];
			}else if(lineArray[2].matches("\\d+")){
				pse.cate = lineArray[2];
			}
			if(lineArray[8].matches("\\d+")){
				pse.postdate = Long.parseLong(lineArray[8]);
			}else{
				pse.postdate = 1469527810;
			}
			if(lineArray[11].matches("\\d+")){
				pse.source = Integer.parseInt(lineArray[11]);
			}
			String localStr = lineArray[13].trim();
			if(localStr.contains(",")){
				String[] localArray = localStr.split(",");
				for(String l: localArray){
					if(l.matches("\\d+") && !pse.local.contains(l)){
						pse.local.add(l);
					}
				}
			}
			String salStr = lineArray[14].trim();
			if(salStr.matches("\\d+")){
				pse.salary = Integer.parseInt(salStr);
				if(pse.salary<1 || pse.salary>10){
					//mising
					pse.salary = 0;
				}
			}else{
				pse.salary = 0;
			}
			if(lineArray[15].matches("\\d+")){
				pse.education = Integer.parseInt(lineArray[15]);
				if(pse.education<1 || pse.education>8){
					//mising
					pse.education = 0;
				}
			}else{
				//mising
				pse.education = 0;
			}
			if(lineArray[16].matches("\\d+")){
				pse.experience = Integer.parseInt(lineArray[16]);
				if(pse.experience!=1 && (pse.experience<4 || pse.experience>9)){
					//missing
					pse.experience = 0;
				}
				if(pse.experience>1){
					pse.experience -= 2;
				}
			}else{
				pse.experience = 0;
			}
			if(lineArray[17].matches("\\d+")){
				pse.trade = Integer.parseInt(lineArray[17]) - 243;
			}
			if(lineArray[18].matches("\\d+")){
				pse.enttype = Integer.parseInt(lineArray[18]) - 1475;
				if(pse.enttype<1 || pse.enttype>10){
					//缺失值
					pse.enttype = 0;
				}
			}else{
				pse.enttype = 0;
			}
			String fuliStr = lineArray[19];
			String[] fuliArray = fuliStr.trim().split("|");
			for(String fl: fuliArray){
				if(fl.matches("\\d+")){
					int fli = Integer.parseInt(fl);
					if(fli>0 && fli<=10){
						pse.fuli.add(fli);
					}
				}
			}
			if(lineArray[20].equals("0") || lineArray[20].equals("1")){
				pse.fresh = Integer.parseInt(lineArray[20]);
			}
			String highlightsStr = lineArray[21];
			if(highlightsStr.contains("|")){
				pse.highlights = highlightsStr.split("|").length;
			}else{
				pse.highlights = 0;
			}
			if(lineArray[22].matches("\\d+")){
				pse.additional = Integer.parseInt(lineArray[22]) - 552495;
			}else{
				pse.additional = 0;
			}
			return pse;
		}

		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString().trim();
			if(inputfile.contains("/Ctr")){
				//历史ctr
				String[] lineArray = line.split("\t");
				context.write(new Text(lineArray[0]), new Text("A\001"+lineArray[1]));
			}else{
				String[] lineArray = line.split("\001");
				PositionStructEntity pse = parseEntity(lineArray);
				context.write(new Text(pse.getInfoid()), new Text("B\001"+pse.toJson()));
			}
		}
	}
	
	public static class PositionInfoStructureReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			int histCtr = -1;
			for(Text val: values){
				String vl = val.toString();
				if(vl.startsWith("A")){
					double histCtrDouble = Double.parseDouble(vl.substring(2));
					histCtr = (int)(histCtrDouble * 1000000);
					break;
				}
			}
			if(histCtr < 0){
				histCtr = 11327;
			}
			for(Text val: values){
				String vl = val.toString();
				if(vl.startsWith("B")){
					vl = vl.substring(2);
					PositionStructEntity pse = PositionStructEntity.fromJson(vl);
					pse.histCtr = histCtr;
					context.write(key, new Text(pse.toJson()));
				}
			}
		}
	}
}
