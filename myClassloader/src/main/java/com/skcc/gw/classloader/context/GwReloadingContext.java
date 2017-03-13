package com.skcc.gw.classloader.context;

public class GwReloadingContext {
	private String classLocation;
	private String acceptClasses = ".*";
	
	public String getClassLocation() {
		return classLocation;
	}
	public void setClassLocation(String classLocation) {
		this.classLocation = classLocation;
	}
	public String getAcceptClasses() {
		return acceptClasses;
	}
	public void setAcceptClasses(String acceptClasses) {
		this.acceptClasses = acceptClasses;
	} 
}
