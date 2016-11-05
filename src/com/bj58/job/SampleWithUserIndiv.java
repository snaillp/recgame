package com.bj58.job;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.bj58.entity.SampleInfoEntity;
import com.bj58.entity.UserIndivEntity;

public class SampleWithUserIndiv {
	/*
	 *  input: sample; 个性化
	 */
	public static class SampleLabelMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			if(inputfile.contains("/")){
				//个性化
				context.write(new Text(lineArray[0]+"\001A"), new Text(lineArray[1]));
			}else if(inputfile.contains("")){
				//sample
				context.write(new Text(lineArray[0]+"\001B"), new Text(lineArray[1]));
			}
		}
	}
	
	public static class SampleLabelReducer extends Reducer<Text, Text, Text, Text> {
		private Map<String, String> local2FullpathMap;
		private Map<String, String> cate2FullpathMap;
		@Override
		protected void setup(Context context) throws IOException, InterruptedException 
		{
			//local to fullpath
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("ds_dict_cmc_local"), "utf-8")); // sep
			String line;
			while ((line = br.readLine()) != null) {
				String[] lineArray = line.split("\001");
				String localid = lineArray[0];
				String localfullpath = lineArray[3];
				local2FullpathMap.put(localid, localfullpath);
			}
			br.close();
			//cate to fullpath
			br = new BufferedReader(new InputStreamReader(new FileInputStream("ds_dict_cmc_category"), "utf-8")); // sep
			while ((line = br.readLine()) != null) {
				String[] lineArray = line.split("\001");
				String cateid = lineArray[0];
				String catefullpath = lineArray[5];
				cate2FullpathMap.put(cateid, catefullpath);
			}
			br.close();
		}
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			UserIndivEntity uie = null;
			List<SampleInfoEntity> sampleList = new ArrayList();
			for(Text val: values){
				String vl = val.toString();
				String vll = vl.substring(2);
				if(vl.startsWith("A")){
					uie = UserIndivEntity.fromJson(vll);
				}else if(vl.startsWith("B")){
					SampleInfoEntity sie = SampleInfoEntity.fromJson(vll);
					sampleList.add(sie);
				}
			}
			//个性化匹配
			if(uie != null){
				//目前只有local，cate的匹配
				for(SampleInfoEntity sie: sampleList){
					userIndivMatch(uie, sie);
					context.write(key, new Text(sie.toJson()));
				}
				
			}
		}
		private void userIndivMatch(UserIndivEntity uie, SampleInfoEntity sie){
			//local match
			List<String> userLocalList = uie.getLocalList();
			List<String> sampleLocalList = sie.getLocal();
			double localmatch = 0.0;
			for(String ulocal: userLocalList){
				for(String slocal: sampleLocalList){
					localmatch += localOcateMatchDegree(ulocal, slocal, local2FullpathMap);
				}
			}
			sie.setLocalmatch(localmatch);
			//cate match
			List<String> userCateList = uie.getCateList();
			String sampleCate = sie.getCate();
			double catematch = 0.0;
			for(String ucate: userCateList){
				catematch += localOcateMatchDegree(ucate, sampleCate, cate2FullpathMap);
			}
			sie.setCatematch(catematch);
		}
		private double localOcateMatchDegree(String ulocal, String slocal, Map<String, String> id2FullpathMap)
		{
			double matchDegree = 0;
			String uFullpath = id2FullpathMap.get(ulocal);
			String[] uFullArray = uFullpath.trim().split(",");
			String sFullpath = id2FullpathMap.get(slocal);
			String[] sFullArray = sFullpath.trim().split(",");
			int sDepth=sFullArray.length;
			int uDepth=uFullArray.length;
			int minDepth = sDepth<uDepth?sDepth:uDepth;
			int matchDepth = minDepth;
			for(; matchDepth>0; --matchDepth){
				if(uFullArray[matchDepth-1].equals(sFullArray[matchDepth-1])){
					break;
				}
			}
			if(matchDepth >= 0){
				if(sDepth != uDepth){
					//u:1,2,3, s:1,2;文档只能匹配到用户的前一级，例如用户是服务员，文档是餐饮，这时候得分为2，因为是非全匹配，-0.5分
					if(minDepth == sDepth){
						matchDegree = matchDepth - 0.5;
					}
					//u:1,2, s:1,2,3;
					if(minDepth == uDepth){
						matchDegree = matchDepth;
					}
				}else{
					matchDegree = matchDepth;
				}
			}
			return matchDegree;
		}
	}
}
