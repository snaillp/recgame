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

import com.bj58.entity.ClickInfoEntity;
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
	
	//2.cookie 个性化->用于训练数据和测试数据的生成
	public static class UserIndivInfoMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] lineArray = line.trim().split("\t");
			ClickInfoEntity cie = ClickInfoEntity.fromJson(lineArray[1]);
			context.write(new Text(cie.getCookie()), new Text(lineArray[1]));
		}
	}
	public static class UserIndivInfoReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			List<String> cateList = new ArrayList();
			List<String> localList = new ArrayList();
			for(Text val: values){
				String vl = val.toString();
				ClickInfoEntity cie = ClickInfoEntity.fromJson(vl);
				String local = cie.getLocal();
				if(!localList.contains(local)){
					localList.add(local);
				}
				String cate = cie.getCate();
				if(!cateList.contains(cate)){
					cateList.add(cate);
				}
			}
			UserIndivEntity uie = new UserIndivEntity();
			uie.setCookie(key.toString());
			if(!localList.isEmpty()){
				uie.setLocalList(localList);
			}
			if(!cateList.isEmpty()){
				uie.setCateList(cateList);
			}
			context.write(key, new Text(uie.toJson()));
		}
	}

}
