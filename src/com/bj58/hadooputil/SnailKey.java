package com.bj58.hadooputil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnailKey implements WritableComparable<SnailKey> {
	 	private static final Logger logger = LoggerFactory.getLogger(SnailKey.class);
	 	private Text oriKey;
	 	private Text key4sort;
	 	
	 	public SnailKey(){
	 		oriKey = new Text();
	 		key4sort = new Text();
	 	}
	 	
		public Text getOriKey() {
			return oriKey;
		}

		public void setOriKey(Text oriKey) {
			this.oriKey = oriKey;
		}

		public Text getKey4sort() {
			return key4sort;
		}

		public void setKey4sort(Text key4sort) {
			this.key4sort = key4sort;
		}

		@Override
		public void readFields(DataInput input) throws IOException {
			this.oriKey.readFields(input);
			this.key4sort.readFields(input);
		}

		@Override
		public void write(DataOutput output) throws IOException {
			this.oriKey.write(output);
			this.key4sort.write(output);
		}

		@Override
		public int compareTo(SnailKey arg0) {
			// TODO Auto-generated method stub
			return this.oriKey.compareTo(arg0.oriKey);
		} 
}
