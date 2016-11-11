package com.bj58.job;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.bj58.entity.ClickInfoEntity;

public class ProcessPostdate {
	public static class ProcessPostdateMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString().trim();
			String[] lineArray = line.split("\t");
			ClickInfoEntity cie = ClickInfoEntity.fromJson(lineArray[1]);
			if(cie.visittime != 1478167925){
				cie.visittime /= 1000;
			}
			context.write(new Text(lineArray[0]), new Text(cie.toJson()));
		}
	}
}
