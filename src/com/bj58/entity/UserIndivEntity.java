package com.bj58.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserIndivEntity {
	public UserIndivEntity(){
		
	}
	public UserIndivEntity(boolean init){
		cateSet = new HashSet(8);
		localSet = new HashSet(8);
		salarySet = new HashSet(16);
		educationSet = new HashSet(16);
		experienceSet = new HashSet(16);
		tradeSet = new HashSet(64); //行业
		enttypeSet = new HashSet(16); //公司性质，私营。。。
		fuliSet = new HashSet(16); //福利
	}
	public void merge(UserIndivEntity uie){
		this.addCate(uie.getCateSet());
		this.addLoca(uie.getLocalSet());
		this.addSalary(uie.getSalarySet());
		this.addEducation(uie.getSalarySet());
		this.addExperience(uie.getExperienceSet());
		this.addTrade(uie.getTradeSet());
		this.addEnttype(uie.getEnttypeSet());
		this.addFuli(uie.getFuliSet());
	}
	//cate
	public void addCate(String cate){
		this.cateSet.add(cate);
	}
	public void addCate(Set<String> cateSet){
		this.cateSet.addAll(cateSet);
	}
	//local
	public void addLocal(String local){
		this.localSet.add(local);
	}
	public void addLoca(Set<String> localSet){
		this.localSet.addAll(cateSet);
	}
	//salary
	public void addSalary(int salary){
		this.salarySet.add(salary);
	}
	public void addSalary(Set<Integer> salarySet){
		this.salarySet.addAll(salarySet);
	}
	//education
	public void addEducation(int education){
		this.educationSet.add(education);
	}
	public void addEducation(Set<Integer> educationSet){
		this.educationSet.addAll(educationSet);
	}
	//experience
	public void addExperience(int experience){
		this.experienceSet.add(experience);
	}
	public void addExperience(Set<Integer> experienceSet){
		this.experienceSet.addAll(experienceSet);
	}
	//trade
	public void addTrade(int trade){
		this.tradeSet.add(trade);
	}
	public void addTrade(Set<Integer> tradeSet){
		this.tradeSet.addAll(tradeSet);
	}
	//enttypeSet
	public void addEnttype(int enttype){
		this.enttypeSet.add(enttype);
	}
	public void addEnttype(Set<Integer> enttypeSet){
		this.enttypeSet.addAll(enttypeSet);
	}
	//fuliSet
	public void addFuli(int fuli){
		this.fuliSet.add(fuli);
	}
	public void addFuli(Set<Integer> fuliSet){
		this.fuliSet.addAll(fuliSet);
	}
	
	public String toJson()
	{
		return gson.toJson(this);
	}
	
	public static UserIndivEntity fromJson(String str)
	{
		return gson.fromJson(str, UserIndivEntity.class);
	}
	
	//getters and setters
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	public Set<String> getLocalSet() {
		return localSet;
	}
	public void setLocalSet(Set<String> localSet) {
		this.localSet = localSet;
	}
	public Set<String> getCateSet() {
		return cateSet;
	}
	public void setCateSet(Set<String> cateSet) {
		this.cateSet = cateSet;
	}
	public Set<Integer> getSalarySet() {
		return salarySet;
	}
	public void setSalarySet(Set<Integer> salarySet) {
		this.salarySet = salarySet;
	}
	public Set<Integer> getEducationSet() {
		return educationSet;
	}
	public void setEducationSet(Set<Integer> educationSet) {
		this.educationSet = educationSet;
	}
	public Set<Integer> getExperienceSet() {
		return experienceSet;
	}
	public void setExperienceSet(Set<Integer> experienceSet) {
		this.experienceSet = experienceSet;
	}
	public Set<Integer> getTradeSet() {
		return tradeSet;
	}
	public void setTradeSet(Set<Integer> tradeSet) {
		this.tradeSet = tradeSet;
	}
	public Set<Integer> getEnttypeSet() {
		return enttypeSet;
	}
	public void setEnttypeSet(Set<Integer> enttypeSet) {
		this.enttypeSet = enttypeSet;
	}
	public Set<Integer> getFuliSet() {
		return fuliSet;
	}
	public void setFuliSet(Set<Integer> fuliSet) {
		this.fuliSet = fuliSet;
	}


	public static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	String cookie;
	Set<String> localSet;
	Set<String> cateSet;
	Set<Integer> salarySet;
	Set<Integer> educationSet;
	Set<Integer> experienceSet;
	Set<Integer> tradeSet; //行业
	Set<Integer> enttypeSet; //公司性质，私营。。。
	Set<Integer> fuliSet; //福利	
}
