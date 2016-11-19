package com.bj58.job;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.bj58.entity.SampleInfoEntity;
import com.bj58.entity.UserIndivEntity;

public class SampleWithUserIndiv {
	/*
	 *  input: sample; 个性化
	 */
	public static class SampleUserIndivMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String inputfile = ((FileSplit)context.getInputSplit()).getPath().toString();
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			if(inputfile.contains("/userindivmerge/")){
				//个性化，key: cookie
				context.write(new Text(lineArray[0]+"\001A"), new Text("A\001"+lineArray[1]));
			}else if(inputfile.contains("/samplewithposlable/")){
				//sample，key：cookie
				context.write(new Text(lineArray[0]+"\001B"), new Text("B\001"+lineArray[1]));
			}
		}
	}
	
	public static class SampleUserIndivReducer extends Reducer<Text, Text, Text, Text> {
		private Map<String, String> local2FullpathMap;
		private Map<String, String> cate2FullpathMap;
		private MultipleOutputs multioutput;
		@Override
		protected void setup(Context context) throws IOException, InterruptedException 
		{
			local2FullpathMap = new HashMap();
			cate2FullpathMap = new HashMap();
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
			multioutput = new MultipleOutputs(context);// 初始化mos
		}
		@Override
		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			multioutput.close();

		}
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			UserIndivEntity uie = null;
//			List<SampleInfoEntity> sampleList = new ArrayList();
			String keyStr = key.toString();
			String realKey = keyStr.substring(0, keyStr.length()-2);
			for(Text val: values){
				String vl = val.toString();
				String vll = vl.substring(2);
				if(vl.startsWith("A")){
					uie = UserIndivEntity.fromJson(vll);
				}else if(vl.startsWith("B")){
					SampleInfoEntity sie = SampleInfoEntity.fromJson(vll);
					if(null != uie){
						userIndivMatch(uie, sie);
					}
					int slot = sie.getSlot();
					multioutput.write(new Text(realKey), new Text(sie.toJson()), "slot"+slot);
//					context.write(new Text(realKey), new Text(sie.toJson()));
				}
			}
		}
		private void userIndivMatch(UserIndivEntity uie, SampleInfoEntity sie){
			//local match
			Set<String> userLocalSet = uie.getLocalSet();
			Set<String> sampleLocalSet = sie.getLocal();
			double localmatch = 0.0;
			for(String ulocal: userLocalSet){
				if(null == ulocal || ulocal.isEmpty()){
					continue;
				}
				for(String slocal: sampleLocalSet){
					if(null == slocal || slocal.isEmpty()){
						continue;
					}
					localmatch = Math.max(localmatch, localOcateMatchDegree(ulocal, slocal, local2FullpathMap));
				}
			}
			sie.setLocalmatch(localmatch);
			//cate match
			Set<String> userCateList = uie.getCateSet();
			String sampleCate = sie.getCate();
			double catematch = 0.0;
			if(null != sampleCate && !sampleCate.isEmpty()){
				for(String ucate: userCateList){
					catematch = Math.max(catematch, localOcateMatchDegree(ucate, sampleCate, cate2FullpathMap));
				}
			}
			sie.setCatematch(catematch);
			//salary match
			Set<Integer> userSalarySet = uie.getSalarySet();
			int sSalary = sie.getSalary();
			sie.setSalarymatch(featureMatchDegree(userSalarySet, sSalary, 1));
			//education
			Set<Integer> userEducationSet = uie.getEducationSet();
			int sEdu = sie.getEducation();
			sie.setEducationmatch(featureMatchDegree(userEducationSet, sEdu, -1));
			//experience
			Set<Integer> userExpSet = uie.getExperienceSet();
			int sExp = sie.getExperience();
			sie.setExperiencematch(featureMatchDegree(userExpSet, sExp, -1));
			//enttype
			Set<Integer> userEntSet = uie.getEnttypeSet();
			int sEnt = sie.getEnttype();
			sie.setEnttypematch(exactMatchDegree(userEntSet, sEnt));
			//trade
			Set<Integer> userTradeSet = uie.getTradeSet();
			int sTrade = sie.getTrade();
			sie.setTradematch(exactMatchDegree(userTradeSet, sTrade));
			//fuli
			Set<Integer> userFuliSet = uie.getFuliSet();
			Set<Integer> sFuliSet = sie.getFuli();
			sie.setFuliMatch(exactMatchDegree(userFuliSet, sFuliSet));
		}
		private double localOcateMatchDegree(String ulocal, String slocal, Map<String, String> id2FullpathMap)
		{
			double matchDegree = 0;
			String uFullpath = id2FullpathMap.get(ulocal);
			if(uFullpath == null || uFullpath.isEmpty()){
				return matchDegree;
			}
			String[] uFullArray = uFullpath.trim().split("\002");
			String sFullpath = id2FullpathMap.get(slocal);
			if(null == sFullpath || sFullpath.isEmpty()){
				return matchDegree;
			}
			String[] sFullArray = sFullpath.trim().split("\002");
			int sDepth=sFullArray.length;
			int uDepth=uFullArray.length;
			int minDepth = sDepth<uDepth?sDepth:uDepth;
			int matchDepth = minDepth;
			for(; matchDepth>0; --matchDepth){
				if(uFullArray[matchDepth-1].equals(sFullArray[matchDepth-1])){
					break;
				}
			}
			if(matchDepth > 0){
				if(sDepth != uDepth){
					//u:1,2,3, s:1,2;文档只能匹配到用户的前一级，例如用户是服务员，文档是餐饮，这时候得分为2，因为是非全匹配，-0.5分
					if(minDepth == sDepth){
						matchDegree = matchDepth - 0.5;
					}else if(minDepth == uDepth){
						//u:1,2, s:1,2,3;
						matchDegree = matchDepth;
					}
				}else{
					if(matchDepth == sDepth){
						//u:1,2,3 s:1,2,3
						matchDegree = matchDepth;
					}else{
						if(matchDepth > 1){
							//u:1,2,3 s:1,2,4, 二级类还可以，如果是一级类匹配，没意义
							matchDegree = matchDepth - 0.5;
						}
					}
				}
			}
			return matchDegree;
		}
		public double featureMatchDegree(Set<Integer> uset, int svalue, int shigh){
			int max = -1;
			int min = 10000;
			double matchDegree = 0.0;
			for(int m: uset){
				if(m>max){
					max = m;
				}
				if(m < min){
					min = m;
				}
			}
			if(uset.contains(svalue)){
				//全完匹配
				matchDegree = 1;
			}else if(shigh==1 &&svalue>=min){
				//例如薪水，doc提供的薪水比用户历史的高，也可以，但不是完全匹配，-0.5
				matchDegree = 0.5;
			}else if(shigh==-1 && svalue<=max){
				//例如学历，doc需要的学历低于用户学历，但不是完全匹配，-0.5
				matchDegree = 0.5;
			}
			return matchDegree;
		}
		public double exactMatchDegree(Set<Integer> uset, int svalue)
		{
			double matchDegree = 0.0;
			if(uset.contains(svalue)){
				matchDegree = 1;
			}
			return matchDegree;
		}
		public double exactMatchDegree(Set<Integer> uset, Set<Integer> sset)
		{
			double matchDegree = 0.0;
			for(int svalue: sset){
				if(uset.contains(svalue)){
					matchDegree += 1;
				}
			}
			return matchDegree;
		}
	}
}
