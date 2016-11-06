package com.bj58.job;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.bj58.entity.ClickInfoEntity;
import com.bj58.entity.PositionStructEntity;
import com.bj58.entity.UserIndivEntity;

public class UserClickInfo {
	/*
	 * 输入为职位的点击展现数据，提取要用的字段，这份数据作为基础数据，后续产出以下两种数据
	 * 	1.infoid lable, infoid的展示时间->用于训练数据的生成
	 * 	2.cookie 个性化->用于训练数据和测试数据的生成
	 */
	public static class UserClickInfoMapper extends Mapper<Object, Text, Text, Text> {
		private Map<String, String> disp2locaMap;
		
		private Map<String, String> disp2cateMap;
		private Map<String, String> catename2cateMap;
		
		@Override
		protected void setup(Context context) throws IOException, InterruptedException 
		{
			//dislocal to local
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("ds_dict_cmc_displocal"), "utf-8")); // sep
			String line;
			while ((line = br.readLine()) != null) {
				String[] lineArray = line.split("\001");
				String dispid = lineArray[0];
				String id = lineArray[3];
				disp2locaMap.put(dispid, id);
			}
			br.close();
			//cate, catename to cateid
			br = new BufferedReader(new InputStreamReader(new FileInputStream("ds_dict_cmc_category"), "utf-8")); // sep
			while ((line = br.readLine()) != null) {
				String[] lineArray = line.split("\001");
				String cateid = lineArray[0];
				String catename = lineArray[3];
				catename2cateMap.put(catename, cateid);
			}
			br.close();
			//dispcate, dispcate to cateid
			br = new BufferedReader(new InputStreamReader(new FileInputStream("ds_dict_cmc_dispcategory"), "utf-8")); // sep
			while ((line = br.readLine()) != null) {
				String[] lineArray = line.split("\001");
				String dispid = lineArray[0];
				String catename = lineArray[9];
				String cateid = lineArray[7];
				if(catename2cateMap.containsKey(catename)){
					disp2cateMap.put(dispid, catename2cateMap.get(catename));
				}else{
					disp2cateMap.put(dispid, cateid);
				}
			}
			br.close();
		}
		private ClickInfoEntity parseEntity(String[] lineArray)
		{
			ClickInfoEntity cie = new ClickInfoEntity();
			cie.cookie = lineArray[0];
			if(lineArray[5].matches("\\d+")){
				cie.visittime = Long.parseLong(lineArray[5]);
			}else{
				cie.visittime = 1478167925;
			}
			String localTemp = null;
			if(lineArray[11].matches("\\d+")){
				localTemp = lineArray[11];
			}else if(lineArray[10].matches("\\d+")){
				localTemp = lineArray[10];
			}else if(lineArray[9].matches("\\d+")){
				localTemp = lineArray[9];
			}else if(lineArray[8].matches("\\d+")){
				localTemp = lineArray[8];
			}
			if(null != localTemp){
				cie.local = disp2locaMap.get(localTemp);
			}
			String cateTemp = null;
			if(lineArray[15].matches("\\d+")){
				cateTemp = lineArray[15];
			}else if(lineArray[14].matches("\\d+")){
				cateTemp = lineArray[14];
			}else if(lineArray[13].matches("\\d+")){
				cateTemp = lineArray[13];
			}else if(lineArray[12].matches("\\d+")){
				cateTemp = lineArray[12];
			}
			if(null != cateTemp){
				//TODO: 查表现类对应的归属类，及归属类的fullpath
				cie.cate = disp2cateMap.get(cateTemp);
			}
			String slotStr = lineArray[19];
			if(slotStr.equals("-")){
				cie.slot = 0;
			}else{
				//TODO: 其他展示位标示
			}
			if(lineArray[22].matches("\\d+")){
				cie.pageno = Integer.parseInt(lineArray[22]);
			}else{
				cie.pageno = 0;
			}
			if(lineArray[23].matches("\\d+")){
				cie.position = Integer.parseInt(lineArray[23]);
			}else{
				cie.position = 0;
			}
			if(lineArray[24].equals("0") || lineArray[24].equals("1")){
				cie.label = lineArray[24];
			}
			return cie;
		}
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString().trim();
			String[] lineArray = line.split("\001");
			ClickInfoEntity cie = parseEntity(lineArray);
			context.write(new Text(cie.getInfoid()), new Text(cie.toJson()));
		}
	}
	
	//2.cookie 个性化: 只获取了user访问的local，category->用于训练数据和测试数据的生成，key为infoid
	public static class UserIndivInfoMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			if(inputfile.contains("/")){
				//帖子数据， key: infoID，value: info
				context.write(new Text(lineArray[0]+"\001A"), new Text("A\001"+lineArray[1]));
			}else if(inputfile.contains("")){
				//点击数据，key: infoid, value：点击数据
//				ClickInfoEntity cie = ClickInfoEntity.fromJson(lineArray[1]);
				context.write(new Text(lineArray[0]+"\001B"), new Text("B\001"+lineArray[1]));
			}
		}
	}
	public static class UserIndivInfoReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			PositionStructEntity pse = null;
			for(Text val: values){
				String vl = val.toString();
				if(vl.startsWith("A")){
					//imc
					pse = PositionStructEntity.fromJson(vl.substring(2));
				}else if(vl.startsWith("B")){
					ClickInfoEntity cie = ClickInfoEntity.fromJson(vl.substring(2));
					UserIndivEntity uie = new UserIndivEntity(true);
					if(null != pse){
						//from show
						uie.setCookie(cie.getCookie());
						uie.addCate(cie.getCate());
						uie.addLocal(cie.getLocal());
						//from doc
						uie.addSalary(pse.getSalary());
						uie.addEducation(pse.getEducation());
						uie.addExperience(pse.getExperience());
						uie.addTrade(pse.getTrade());
						uie.addEnttype(pse.getEnttype());
						uie.addFuli(pse.getFuli());
						context.write(new Text(uie.getCookie()), new Text(uie.toJson()));
					}
				}
			}
		}
	}
	//3.聚合user个性化数据, input:cookie\tcookie_doc
	public static class UserIndivMergerMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			context.write(new Text(lineArray[0]), new Text(lineArray[1]));
		}
	}
	public static class UserIndivMergerReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			UserIndivEntity uie = new UserIndivEntity(true);
			for(Text val: values){
				String vl = val.toString();
				UserIndivEntity uieVl = UserIndivEntity.fromJson(vl);
				uie.merge(uieVl);
			}
			context.write(key, new Text(uie.toJson()));
		}
	}
}
