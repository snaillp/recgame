package com.bj58.job;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.fs.FileSystem;
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
			disp2locaMap = new HashMap();
			disp2cateMap = new HashMap();
			catename2cateMap = new HashMap();
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
			if(lineArray[0].length() < 5){
				return null;
			}
			ClickInfoEntity cie = new ClickInfoEntity();
			cie.cookie = lineArray[0];
			if(!lineArray[3].isEmpty()){
				cie.sid = lineArray[3];
			}
			if(lineArray[4].matches("\\d+")){
				//单位为小时
				cie.visittime = Long.parseLong(lineArray[4])/(5*60*1000);
			}else{
				cie.visittime = 1478167925/(5*60);
			}
			String localTemp = null;
			if(lineArray[10].matches("\\d+")){
				localTemp = lineArray[10];
			}else if(lineArray[9].matches("\\d+")){
				localTemp = lineArray[9];
			}else if(lineArray[8].matches("\\d+")){
				localTemp = lineArray[8];
			}else if(lineArray[7].matches("\\d+")){
				localTemp = lineArray[7];
			}
			if(null != localTemp){
				cie.local = disp2locaMap.get(localTemp);
			}
			String cateTemp = null;
			if(lineArray[14].matches("\\d+")){
				cateTemp = lineArray[14];
			}else if(lineArray[13].matches("\\d+")){
				cateTemp = lineArray[13];
			}else if(lineArray[12].matches("\\d+")){
				cateTemp = lineArray[12];
			}else if(lineArray[11].matches("\\d+")){
				cateTemp = lineArray[11];
			}
			if(null != cateTemp){
				//查表现类对应的归属类，及归属类的fullpath
				cie.cate = disp2cateMap.get(cateTemp);
			}
			String slotStr = lineArray[18].trim();
			if(slotStr.equals("-")){
				cie.slot = 0;
			}else if(slotStr.equals("m_detail")){
				cie.slot = 1;
			}else if(slotStr.equals("resume_post_success")){
				cie.slot = 2;
			}else if(slotStr.equals("m404pag")){
				cie.slot = 3;
			}else if(slotStr.equals("m_aggregation")){
				cie.slot = 4;
			}else if(slotStr.equals("m_detail_near")){
				cie.slot = 5;
			}else if(slotStr.equals("resume_delivery_success")){
				cie.slot = 6;
			}
			String infoid = lineArray[19];
			if(infoid.length() > 5){
				cie.infoid = infoid;
			}
			if(lineArray[21].matches("\\d+")){
				cie.pageno = Integer.parseInt(lineArray[21]);
			}else{
				cie.pageno = 0;
			}
			if(lineArray[22].matches("\\d+")){
				cie.position = Integer.parseInt(lineArray[22]);
			}else{
				cie.position = 0;
			}
			if(lineArray[23].equals("0") || lineArray[23].equals("1")){
				cie.label = lineArray[23];
			}
			return cie;
		}
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString().trim();
			String[] lineArray = line.split("\001");
			ClickInfoEntity cie = parseEntity(lineArray);
			if(null == cie){
				return;
			}
			String infoid = cie.getInfoid();
			if(null != infoid){
				context.write(new Text(cie.getSid()), new Text(cie.toJson()));
			}
		}
	}
	public static class UserClickInfoReducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			//以query为单位，去掉全0和全1的
			List<ClickInfoEntity> entityList = new ArrayList();
			int count0 = 0;
			int count1 = 0;
			int first1Pos = 0;
			int lineno = 0;
			for(Text val: values){
				String vl = val.toString();
				ClickInfoEntity cie = ClickInfoEntity.fromJson(vl);
				if(cie.getPageno() > 1){
					continue;
				}
				lineno++;
				//丢弃超过30的位置
//				if(lineno > 30){
//					break;
//				}
				if(cie.getLabel().equals("0")){
					count0++;
				}else{
					if(first1Pos == 0){
						first1Pos = lineno;
					}
					count1++;
				}
				entityList.add(cie);
			}
			int entityNum = entityList.size();
			if(count0 == entityNum || count1 == entityNum){
				return;
			}
			for(ClickInfoEntity val: entityList){
				context.write(new Text(val.getInfoid()), new Text(val.toJson()));
			}
		}
	}
	
	//2.cookie 个性化: 只获取了user访问的local，category->用于训练数据和测试数据的生成，key为infoid
	public static class UserIndivInfoMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			if(inputfile.contains("/positionuserctr/")){
				//帖子数据， key: infoID，value: info
				context.write(new Text(lineArray[0]+"\001A"), new Text("A\001"+lineArray[1]));
			}else if(inputfile.contains("/UserClickInfo/")){
				//点击数据，key: infoid, value：点击数据
//				ClickInfoEntity cie = ClickInfoEntity.fromJson(lineArray[1]);
				context.write(new Text(lineArray[0]+"\001C"), new Text("C\001"+lineArray[1]));
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
					if(pse == null){
						pse = PositionStructEntity.fromJson(vl.substring(2));
					}else{
						PositionStructEntity pseN = PositionStructEntity.fromJson(vl.substring(2));
						if(pseN.getPostdate() > pse.getPostdate()){
							pse = pseN;
						}
					}
				}else if(vl.startsWith("C")){
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
