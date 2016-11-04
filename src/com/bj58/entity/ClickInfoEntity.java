package com.bj58.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ClickInfoEntity {
	public ClickInfoEntity(){
		
	}
	public String toJson()
	{
		return gson.toJson(this);
	}
	
	public static ClickInfoEntity fromJson(String str)
	{
		return gson.fromJson(str, ClickInfoEntity.class);
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getInfoid() {
		return infoid;
	}
	public void setInfoid(String infoid) {
		this.infoid = infoid;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public long getVisittime() {
		return visittime;
	}
	public void setVisittime(long visittime) {
		this.visittime = visittime;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getCate() {
		return cate;
	}
	public void setCate(String cate) {
		this.cate = cate;
	}
	public int getSlot() {
		return slot;
	}
	public void setSlot(int slot) {
		this.slot = slot;
	}
	public int getPageno() {
		return pageno;
	}
	public void setPageno(int pageno) {
		this.pageno = pageno;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	public String cookie;
	public String infoid;
	public String label;
	public long visittime;
	public String local;
	public String cate;
	public int slot; //推荐位标示
	public int pageno; //职位所在页码
	public int position; //职位所在位置
}
